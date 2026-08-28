package com.nineteens.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class ProfileDtos {

    private ProfileDtos() {
    }

    public record UpdateProfileRequest(
            @NotBlank @Size(max = 100) String firstName,
            @NotBlank @Size(max = 100) String lastName,
            @Size(max = 30) String phone
    ) {
    }

    public record AddressRequest(
            @NotBlank @Size(max = 150) String recipientName,
            @NotBlank @Size(max = 30) String phone,
            @NotBlank @Size(max = 500) String addressLine,
            @NotBlank @Size(max = 100) String city,
            @NotBlank @Size(max = 20) String postalCode,
            boolean defaultAddress
    ) {
    }

    public record AddressResponse(
            Long id,
            String recipientName,
            String phone,
            String addressLine,
            String city,
            String postalCode,
            boolean defaultAddress
    ) {
    }
}
