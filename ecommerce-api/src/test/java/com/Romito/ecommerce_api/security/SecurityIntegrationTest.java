package com.Romito.ecommerce_api.security;

import com.Romito.ecommerce_api.dto.ProductRequest;
import com.Romito.ecommerce_api.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase

class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @Test
    void listarProductosEsPublico() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());
    }

    @Test
    void crearProductoSinTokenDevuelve403() throws Exception {
        ProductRequest request = new ProductRequest(
                "Mouse", null, new BigDecimal("10000.00"), 5, 1L);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void usuarioConRolUserNoPuedeCrearProducto() throws Exception {
        String token = registrarYObtenerToken("mperez", "mperez@example.com", "password123");

        ProductRequest request = new ProductRequest(
                "Mouse", null, new BigDecimal("10000.00"), 5, 1L);

        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
   @Test
    void adminPuedeCrearProducto() throws Exception {
        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"admin","password":"Admin123!"}
                                """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = jsonMapper.readTree(loginResponse).get("token").asText();

        String categoryResponse = mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Periféricos"}
                                """))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long categoryId = jsonMapper.readTree(categoryResponse).get("id").asLong();

        ProductRequest request = new ProductRequest(
                "Teclado mecánico", "Switches rojos",
                new BigDecimal("45000.00"), 10, categoryId);

        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
    private String registrarYObtenerToken(String username, String email, String password) throws Exception {
        RegisterRequest request = new RegisterRequest(username, email, password);

        String responseJson = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        var node = jsonMapper.readTree(responseJson);
        return node.get("token").asText();
    }
}