package com.tanpuh.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record IntrospectRequest( @NotBlank(message = "TOKEN_BLANK") String token ) { }