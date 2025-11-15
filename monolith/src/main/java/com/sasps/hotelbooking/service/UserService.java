package com.sasps.hotelbooking.service;

import com.sasps.hotelbooking.dto.AuthResponse;
import com.sasps.hotelbooking.dto.LoginRequest;
import com.sasps.hotelbooking.dto.RegisterRequest;
import com.sasps.hotelbooking.model.User;
import com.sasps.hotelbooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        
        // Determine role (default to CUSTOMER if not provided or invalid)
        User.Role userRole = User.Role.CUSTOMER;
        if (request.getRole() != null) {
            try {
                userRole = User.Role.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Invalid role, default to CUSTOMER
                userRole = User.Role.CUSTOMER;
            }
        }
        
        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty() 
                    ? request.getPhoneNumber() : null)
                .role(userRole)
                .enabled(true)
                .emailVerified(false)
                .build();
        
        user = userRepository.save(user);
        
        return AuthResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .message("User registered successfully")
                .build();
    }
    
    public AuthResponse login(LoginRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        
        // Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        
        // Check if user is enabled
        if (!user.getEnabled()) {
            throw new RuntimeException("User account is disabled");
        }
        
        return AuthResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .message("Login successful")
                .build();
    }
}
