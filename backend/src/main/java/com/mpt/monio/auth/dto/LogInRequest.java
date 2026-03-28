package com.mpt.monio.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

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