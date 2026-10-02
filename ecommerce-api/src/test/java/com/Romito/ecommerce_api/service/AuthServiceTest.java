package com.Romito.ecommerce_api.service;

import com.Romito.ecommerce_api.dto.AuthResponse;
import com.Romito.ecommerce_api.dto.LoginRequest;
import com.Romito.ecommerce_api.dto.RegisterRequest;
import com.Romito.ecommerce_api.model.AppUser;
import com.Romito.ecommerce_api.model.Role;
import com.Romito.ecommerce_api.repository.AppUserRepository;
import com.Romito.ecommerce_api.repository.RoleRepository;
import com.Romito.ecommerce_api.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private AppUserRepository appUserRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void registraUnUsuarioNuevoYDevuelveToken() {
        RegisterRequest request = new RegisterRequest("jromito", "j@example.com", "password123");
        Role userRole = new Role();
        userRole.setId(2L);
        userRole.setName("USER");

        when(appUserRepository.existsByUsername("jromito")).thenReturn(false);
        when(appUserRepository.existsByEmail("j@example.com")).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode("password123")).thenReturn("hash-simulado");
        when(jwtService.generateToken("jromito", "USER")).thenReturn("token-simulado");

        AuthResponse response = authService.register(request);

        assertEquals("token-simulado", response.token());
        assertEquals("USER", response.role());
        verify(appUserRepository).save(any(AppUser.class));
    }

    @Test
    void registraRechazaUsernameDuplicado() {
        RegisterRequest request = new RegisterRequest("jromito", "j@example.com", "password123");
        when(appUserRepository.existsByUsername("jromito")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
        verify(appUserRepository, never()).save(any());
    }

    @Test
    void loginConCredencialesCorrectasDevuelveToken() {
        LoginRequest request = new LoginRequest("jromito", "password123");

        Role userRole = new Role();
        userRole.setName("USER");

        AppUser user = new AppUser();
        user.setUsername("jromito");
        user.setPassword("hash-guardado");
        user.setRole(userRole);

        when(appUserRepository.findByUsername("jromito")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hash-guardado")).thenReturn(true);
        when(jwtService.generateToken("jromito", "USER")).thenReturn("token-simulado");

        AuthResponse response = authService.login(request);

        assertEquals("token-simulado", response.token());
    }

    @Test
    void loginConContraseñaIncorrectaLanzaExcepcion() {
        LoginRequest request = new LoginRequest("jromito", "incorrecta");

        Role userRole = new Role();
        userRole.setName("USER");

        AppUser user = new AppUser();
        user.setUsername("jromito");
        user.setPassword("hash-guardado");
        user.setRole(userRole);

        when(appUserRepository.findByUsername("jromito")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("incorrecta", "hash-guardado")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }
}