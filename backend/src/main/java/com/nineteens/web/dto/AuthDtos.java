package com.nineteens.web.dto;

import com.nineteens.domain.user.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record RegisterRequest(
            @Email @NotBlank String email,
            @NotBlank @Size(min = 8, max = 72) String password,
            @NotBlank @Size(max = 100) String firstName,
            @NotBlank @Size(max = 100) String lastName,
            @Size(max = 30) String phone
    ) {
    }

    public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {
    }

    public record UserSummary(
            Long id,
            String email,
            String firstName,
            String lastName,
            String phone,
            Role role,
            String status
    ) {
    }

    public record AuthResponse(String accessToken, String tokenType, long expiresIn, UserSummary user) {
    }
}
