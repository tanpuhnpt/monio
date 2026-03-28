package com.mpt.monio.auth.service;

import com.mpt.monio.exception.AppException;
import com.mpt.monio.exception.ErrorCode;
import com.mpt.monio.auth.dto.*;
import com.mpt.monio.auth.entity.InvalidatedToken;
import com.mpt.monio.auth.entity.User;
import com.mpt.monio.auth.mapper.UserMapper;
import com.mpt.monio.auth.repo.InvalidatedTokenRepository;
import com.mpt.monio.auth.repo.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthServiceImpl implements AuthService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.signer-key}")
    String signerKey;

    @NonFinal
    @Value("${jwt.valid-duration}")
    Duration validDuration;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    Duration refreshableDuration;

    @NonFinal
    @Value("${spring.application.name}")
    String appName;

    @Override
    public AuthResponse signUp(SignUpRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail()))
            throw new AppException(ErrorCode.USER_EXISTED);

        User user = userMapper.toEntity(signUpRequest);
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        userRepository.save(user);

        return new AuthResponse(generateToken(user.getId().toString()));
    }

    @Override
    public AuthResponse authenticate(LogInRequest logInRequest) {
        User user = userRepository.findByEmail(logInRequest.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        boolean isAuthenticated = passwordEncoder.matches(logInRequest.getPassword(), user.getPassword());
        if (!isAuthenticated) throw new AppException(ErrorCode.UNAUTHENTICATED);

        return new AuthResponse(generateToken(user.getId().toString()));
    }

    @Override
    public IntrospectResponse introspect(IntrospectRequest introspectRequest) throws JOSEException, ParseException {
        boolean isValid = true;

        try {
            verifyToken(introspectRequest.getToken(), false);
        } catch (AppException e) {
            isValid = false;
        }

        return new IntrospectResponse(isValid);
    }

    @Override
    public void logOut(LogoutRequest logoutRequest) throws ParseException, JOSEException {
        try {
            var signedJwt = verifyToken(logoutRequest.getToken(), true);
            invalidateToken(signedJwt);
        } catch (AppException e) {
            log.error("Token is already expired");
        }
    }

    @Override
    public AuthResponse refreshToken(RefreshRequest refreshRequest) throws ParseException, JOSEException {
        var signedJwt = verifyToken(refreshRequest.getToken(), true);
        invalidateToken(signedJwt);

        String userIdFromJwt = signedJwt.getJWTClaimsSet().getSubject();

        Long userId = Long.valueOf(userIdFromJwt);
        if (!userRepository.existsById(userId))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return new AuthResponse(generateToken(userIdFromJwt));
    }

    private String generateToken(String userId) {
        // tạo header
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        // tạo claimset, các field trong payload gọi là claim
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(userId)
                .issuer(appName)
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plus(validDuration)))
                .jwtID(UUID.randomUUID().toString())
                .build();

        // tạo payload
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        // tạo jwsobject, jwsobject tương ứng với cấu trúc của token, gồm header và payload
        JWSObject jwsObject = new JWSObject(header, payload);

        // kí token
        try {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot generate token", e);
            throw new RuntimeException(e);
        }
    }

    private SignedJWT verifyToken(String token, boolean isRefreshable) throws JOSEException, ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token); // tách chuỗi JWT thành 3 phần để tạo 1 obj SignedJWT

        JWSVerifier verifier = new MACVerifier(signerKey.getBytes());
        boolean isVerified = signedJWT.verify(verifier); // xác minh chữ ký của JWT

        // ktra token có hợp lệ hay không
        // nếu có thì ktra token còn hiệu lực hay không (so sánh với tgian hiện tại)
        Date expTime = (isRefreshable)
                ? Date.from(signedJWT
                .getJWTClaimsSet()
                .getIssueTime()
                .toInstant()
                .plus(refreshableDuration))
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        if (!(isVerified && expTime.after(Date.from(Instant.now()))))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        String jti = signedJWT.getJWTClaimsSet().getJWTID();
        if (invalidatedTokenRepository.existsById(jti))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }

    private void invalidateToken(SignedJWT signedJwt) throws ParseException {
        String jti = signedJwt.getJWTClaimsSet().getJWTID();
        Date expTime = signedJwt.getJWTClaimsSet().getExpirationTime();

        // tính số giây còn lại từ bây giờ cho đến lúc token thực sự hết hạn
        long ttl = ChronoUnit.SECONDS.between(Instant.now(), expTime.toInstant());

        // nếu ttlSeconds <= 0 tức là token đã quá hạn rồi thì ko cần lưu
        if (ttl > 0)
            invalidatedTokenRepository.save(new InvalidatedToken(jti, ttl));
    }
}
