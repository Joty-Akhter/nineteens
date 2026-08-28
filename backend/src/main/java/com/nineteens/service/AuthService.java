package com.nineteens.service;

import com.nineteens.common.exception.ConflictException;
import com.nineteens.domain.user.Role;
import com.nineteens.domain.user.User;
import com.nineteens.domain.user.UserRepository;
import com.nineteens.domain.user.UserStatus;
import com.nineteens.security.JwtService;
import com.nineteens.security.UserPrincipal;
import com.nineteens.web.dto.AuthDtos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ConflictException("An account with this email already exists");
        }
        User user = new User();
        user.setEmail(request.email().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setPhone(request.phone());
        user.setRole(Role.USER);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        log.info("Registered user {}", user.getEmail());
        return toAuthResponse(user);
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        User user = userRepository
                .findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new ConflictException("Invalid email or password"));
        return toAuthResponse(user);
    }

    public AuthDtos.AuthResponse toAuthResponse(User user) {
        UserPrincipal principal = UserPrincipal.from(user);
        String token = jwtService.generateAccessToken(principal);
        return new AuthDtos.AuthResponse(token, "Bearer", jwtService.expirationMs(), toSummary(user));
    }

    public static AuthDtos.UserSummary toSummary(User user) {
        return new AuthDtos.UserSummary(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getRole(),
                user.getStatus().name());
    }
}
