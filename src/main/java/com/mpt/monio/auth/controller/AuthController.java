package com.mpt.monio.auth.controller;

import com.mpt.monio.auth.dto.*;
import com.mpt.monio.auth.service.AuthService;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/sign-up")
    @Operation(summary = "Sign up")
    public ResponseEntity<?> signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        return ResponseEntity.ok(authService.signUp(signUpRequest));
    }

    @PostMapping("/log-in")
    @Operation(summary = "Log in")
    public ResponseEntity<?> authenticate(@RequestBody @Valid LogInRequest logInRequest) {
        return ResponseEntity.ok(authService.authenticate(logInRequest));
    }

    @PostMapping("/introspect")
    @Operation(summary = "Verify token")
    public ResponseEntity<?> introspect(@RequestBody IntrospectRequest introspectRequest) throws ParseException, JOSEException {
        return ResponseEntity.ok(authService.introspect(introspectRequest));
    }
}
