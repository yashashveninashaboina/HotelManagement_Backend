package com.hotelmanagement.service;

import com.hotelmanagement.dto.AuthResponse;
import com.hotelmanagement.dto.LoginRequest;
import com.hotelmanagement.dto.RegisterRequest;
import com.hotelmanagement.config.JwtTokenProvider;
import com.hotelmanagement.exception.InvalidBookingException;
import com.hotelmanagement.exception.ResourceNotFoundException;
import com.hotelmanagement.model.Customer;
import com.hotelmanagement.model.Role;
import com.hotelmanagement.model.User;
import com.hotelmanagement.repository.CustomerRepository;
import com.hotelmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new InvalidBookingException("Email is already registered: " + request.getEmail());
        }

        Role userRole = request.getRole() != null ? request.getRole() : Role.CUSTOMER;

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .build();

        userRepository.save(user);

        // If user is a customer, automatically create a customer profile
        if (userRole == Role.CUSTOMER) {
            Customer customer = Customer.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .phone(request.getPhone() != null ? request.getPhone() : "N/A")
                    .address(request.getAddress() != null ? request.getAddress() : "N/A")
                    .build();
            customerRepository.save(customer);
        }

        // Authenticate new user and generate token
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .message("User registered successfully")
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .message("Login successful")
                .build();
    }
}
