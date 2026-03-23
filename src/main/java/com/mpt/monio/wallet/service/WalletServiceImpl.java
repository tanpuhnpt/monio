package com.mpt.monio.wallet.service;

import com.mpt.monio.auth.entity.User;
import com.mpt.monio.auth.repo.UserRepository;
import com.mpt.monio.exception.AppException;
import com.mpt.monio.exception.ErrorCode;
import com.mpt.monio.redis.RedisService;
import com.mpt.monio.wallet.dto.CreateWalletRequest;
import com.mpt.monio.wallet.dto.UpdateWalletRequest;
import com.mpt.monio.wallet.dto.WalletResponse;
import com.mpt.monio.wallet.entity.Wallet;
import com.mpt.monio.wallet.mapper.WalletMapper;
import com.mpt.monio.wallet.repo.WalletRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class WalletServiceImpl implements WalletService {
    WalletRepository walletRepository;
    WalletMapper walletMapper;
    UserRepository userRepository;
    RedisService redisService;

    @Override
    public List<WalletResponse> getAllWallets() {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());

        String key = String.format("wallet:user:%d", userId);
        List<WalletResponse> responses = redisService.getAll(key, WalletResponse.class);

        if (responses == null) {
            log.info("query wallet");
            responses = walletRepository
                    .findAllByUserIdAndIsActiveTrue(userId)
                    .stream().map(walletMapper::toResponse)
                    .toList();

            redisService.saveAll(key, responses);
        }

        return responses;
    }

    @Override
    public WalletResponse getWallet(Long id) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());

        Wallet wallet = walletRepository.findByIdAndUserIdAndIsActiveTrue(id, userId)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        return walletMapper.toResponse(wallet);
    }

    @Override
    public WalletResponse createWallet(CreateWalletRequest createWalletRequest) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Wallet wallet = walletMapper.toEntity(createWalletRequest);
        wallet.setUser(user);

        try {
            return walletMapper.toResponse(walletRepository.save(wallet));
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.WALLET_EXISTED);
        }
    }

    @Override
    public Object updateWallet(Long id, UpdateWalletRequest updateWalletRequest) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());

        Wallet wallet = walletRepository.findByIdAndUserIdAndIsActiveTrue(id, userId)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        wallet.setName(updateWalletRequest.getName());
        wallet.setCurrency(updateWalletRequest.getCurrency());

        try {
            return walletMapper.toResponse(walletRepository.save(wallet));
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.WALLET_EXISTED);
        }
    }

    @Override
    public void deleteWallet(Long id) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());

        Wallet wallet = walletRepository.findByIdAndUserIdAndIsActiveTrue(id, userId)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        wallet.setActive(false);
        walletRepository.save(wallet);
    }
}
