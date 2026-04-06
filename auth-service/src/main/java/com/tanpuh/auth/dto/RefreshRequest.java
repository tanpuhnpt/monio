package com.tanpuh.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest ( @NotBlank(message = "TOKEN_BLANK") String token ){}
