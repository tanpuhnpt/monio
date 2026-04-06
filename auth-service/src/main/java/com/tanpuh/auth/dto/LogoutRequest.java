package com.tanpuh.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest ( @NotBlank(message = "TOKEN_BLANK") String token ) {}
