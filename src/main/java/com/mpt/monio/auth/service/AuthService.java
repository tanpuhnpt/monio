package com.mpt.monio.auth.service;

import com.mpt.monio.auth.dto.*;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthService {
    AuthResponse signUp(SignUpRequest signUpRequest);

    AuthResponse authenticate(LogInRequest logInRequest);

    IntrospectResponse introspect(IntrospectRequest introspectRequest) throws JOSEException, ParseException;
}
