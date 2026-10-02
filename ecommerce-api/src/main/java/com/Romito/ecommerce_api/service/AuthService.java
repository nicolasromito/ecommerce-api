package com.Romito.ecommerce_api.service;

import com.Romito.ecommerce_api.dto.AuthResponse;
import com.Romito.ecommerce_api.dto.LoginRequest;
import com.Romito.ecommerce_api.dto.RegisterRequest;
import com.Romito.ecommerce_api.model.AppUser;
import com.Romito.ecommerce_api.model.Role;
import com.Romito.ecommerce_api.repository.AppUserRepository;
import com.Romito.ecommerce_api.repository.RoleRepository;
import com.Romito.ecommerce_api.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AppUserRepository appUserRepository,
                        RoleRepository roleRepository,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService) {
        this.appUserRepository = appUserRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (appUserRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("El username ya está en uso");
        }
        if (appUserRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("El email ya está en uso");
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new NoSuchElementException("Rol USER no encontrado"));

        AppUser user = new AppUser();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(userRole);

        appUserRepository.save(user);

        String token = jwtService.generateToken(user.getUsername(), user.getRole().getName());
        return new AuthResponse(token, user.getUsername(), user.getRole().getName());
    }

    public AuthResponse login(LoginRequest request) {
        AppUser user = appUserRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("Usuario o contraseña incorrectos"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Usuario o contraseña incorrectos");
        }

        String token = jwtService.generateToken(user.getUsername(), user.getRole().getName());
        return new AuthResponse(token, user.getUsername(), user.getRole().getName());
    }
}