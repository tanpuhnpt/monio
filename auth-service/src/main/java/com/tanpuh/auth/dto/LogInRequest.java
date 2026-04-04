package com.tanpuh.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LogInRequest {
    @NotBlank(message = "EMAIL_BLANK")
    private String email;

    @NotBlank(message = "PASSWORD_BLANK")
    private String password;
}