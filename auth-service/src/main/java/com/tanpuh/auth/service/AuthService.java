package com.tanpuh.auth.service;

import com.tanpuh.auth.dto.*;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthService {
    AuthResponse signUp(SignUpRequest signUpRequest);

    AuthResponse authenticate(LogInRequest logInRequest);

    IntrospectResponse introspect(IntrospectRequest introspectRequest) throws JOSEException, ParseException;

    void logOut(LogoutRequest logoutRequest) throws ParseException, JOSEException;

    AuthResponse refreshToken(RefreshRequest refreshRequest) throws ParseException, JOSEException;
}
