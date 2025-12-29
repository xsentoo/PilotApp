package com.pilot.pilot_api.service;

import com.pilot.pilot_api.component.JwtUtil; // Ton outil
import com.pilot.pilot_api.dtos.AuthenticationRequest;
import com.pilot.pilot_api.dtos.AuthenticationResponse;
import com.pilot.pilot_api.dtos.RegisterRequest;
import com.pilot.pilot_api.entities.Role;
import com.pilot.pilot_api.entities.User;
import com.pilot.pilot_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        repository.save(user);
        var jwtToken = jwtUtil.generateToken(user.getUsername());
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtUtil.generateToken(user.getUsername());
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}