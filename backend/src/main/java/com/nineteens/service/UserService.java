package com.nineteens.service;

import com.nineteens.common.exception.NotFoundException;
import com.nineteens.domain.user.Address;
import com.nineteens.domain.user.AddressRepository;
import com.nineteens.domain.user.User;
import com.nineteens.domain.user.UserRepository;
import com.nineteens.domain.user.UserStatus;
import com.nineteens.security.CurrentUser;
import com.nineteens.web.dto.AuthDtos;
import com.nineteens.web.dto.ProfileDtos;
import com.nineteens.web.dto.UpdateUserStatusRequest;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final CurrentUser currentUser;

    public UserService(UserRepository userRepository, AddressRepository addressRepository, CurrentUser currentUser) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.currentUser = currentUser;
    }

    public User require(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public AuthDtos.UserSummary profile() {
        return AuthService.toSummary(require(currentUser.id()));
    }

    @Transactional
    public AuthDtos.UserSummary updateProfile(ProfileDtos.UpdateProfileRequest request) {
        User user = require(currentUser.id());
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setPhone(request.phone());
        return AuthService.toSummary(user);
    }

    public List<ProfileDtos.AddressResponse> addresses() {
        return addressRepository.findByUserIdOrderByDefaultAddressDescCreatedAtDesc(currentUser.id()).stream()
                .map(this::toAddress)
                .toList();
    }

    @Transactional
    public ProfileDtos.AddressResponse createAddress(ProfileDtos.AddressRequest request) {
        User user = require(currentUser.id());
        if (request.defaultAddress()) {
            addressRepository.clearDefaultForUser(user.getId());
        }
        Address address = new Address();
        address.setUser(user);
        apply(address, request);
        addressRepository.save(address);
        return toAddress(address);
    }

    @Transactional
    public ProfileDtos.AddressResponse updateAddress(Long id, ProfileDtos.AddressRequest request) {
        Address address = addressRepository
                .findByIdAndUserId(id, currentUser.id())
                .orElseThrow(() -> new NotFoundException("Address not found"));
        if (request.defaultAddress()) {
            addressRepository.clearDefaultForUser(currentUser.id());
        }
        apply(address, request);
        return toAddress(address);
    }

    @Transactional
    public void deleteAddress(Long id) {
        Address address = addressRepository
                .findByIdAndUserId(id, currentUser.id())
                .orElseThrow(() -> new NotFoundException("Address not found"));
        addressRepository.delete(address);
    }

    public List<AuthDtos.UserSummary> listUsers() {
        return userRepository.findAll().stream().map(AuthService::toSummary).toList();
    }

    @Transactional
    public AuthDtos.UserSummary updateStatus(Long id, UpdateUserStatusRequest request) {
        User user = require(id);
        user.setStatus(request.status() == null ? UserStatus.INACTIVE : request.status());
        return AuthService.toSummary(user);
    }

    private void apply(Address address, ProfileDtos.AddressRequest request) {
        address.setRecipientName(request.recipientName().trim());
        address.setPhone(request.phone().trim());
        address.setAddressLine(request.addressLine().trim());
        address.setCity(request.city().trim());
        address.setPostalCode(request.postalCode().trim());
        address.setDefaultAddress(request.defaultAddress());
    }

    private ProfileDtos.AddressResponse toAddress(Address address) {
        return new ProfileDtos.AddressResponse(
                address.getId(),
                address.getRecipientName(),
                address.getPhone(),
                address.getAddressLine(),
                address.getCity(),
                address.getPostalCode(),
                address.isDefaultAddress());
    }
}
