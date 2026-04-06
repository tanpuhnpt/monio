package com.tanpuh.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LogInRequest (
    @NotBlank(message = "EMAIL_BLANK")
    String email,

    @NotBlank(message = "PASSWORD_BLANK")
    String password
) {}