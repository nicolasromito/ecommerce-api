package com.Romito.ecommerce_api.controller;

import com.Romito.ecommerce_api.dto.AuthResponse;
import com.Romito.ecommerce_api.dto.LoginRequest;
import com.Romito.ecommerce_api.dto.RegisterRequest;
import com.Romito.ecommerce_api.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;
import com.Romito.ecommerce_api.security.JwtAuthenticationFilter;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void registraUsuarioDevuelve201ConToken() throws Exception {
        RegisterRequest request = new RegisterRequest("jromito", "j@example.com", "password123");
        AuthResponse response = new AuthResponse("token-simulado", "jromito", "USER");

        when(authService.register(request)).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("token-simulado"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void registraConUsernameEnBlancoDevuelve400() throws Exception {
        RegisterRequest invalido = new RegisterRequest("", "j@example.com", "password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginCorrectoDevuelve200ConToken() throws Exception {
        LoginRequest request = new LoginRequest("jromito", "password123");
        AuthResponse response = new AuthResponse("token-simulado", "jromito", "USER");

        when(authService.login(request)).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-simulado"));
    }
}