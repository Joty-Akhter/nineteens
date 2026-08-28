package com.nineteens.web;

import com.nineteens.service.UserService;
import com.nineteens.web.dto.AuthDtos;
import com.nineteens.web.dto.ProfileDtos;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public AuthDtos.UserSummary me() {
        return userService.profile();
    }

    @PutMapping
    public AuthDtos.UserSummary update(@Valid @RequestBody ProfileDtos.UpdateProfileRequest request) {
        return userService.updateProfile(request);
    }

    @GetMapping("/addresses")
    public List<ProfileDtos.AddressResponse> addresses() {
        return userService.addresses();
    }

    @PostMapping("/addresses")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileDtos.AddressResponse createAddress(@Valid @RequestBody ProfileDtos.AddressRequest request) {
        return userService.createAddress(request);
    }

    @PutMapping("/addresses/{id}")
    public ProfileDtos.AddressResponse updateAddress(
            @PathVariable Long id, @Valid @RequestBody ProfileDtos.AddressRequest request) {
        return userService.updateAddress(id, request);
    }

    @DeleteMapping("/addresses/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(@PathVariable Long id) {
        userService.deleteAddress(id);
    }
}
