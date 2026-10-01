package com.Romito.ecommerce_api.controller;

import org.springframework.context.annotation.Import;
import com.Romito.ecommerce_api.config.SecurityConfig;
import tools.jackson.databind.json.JsonMapper;
import com.Romito.ecommerce_api.dto.ProductRequest;
import com.Romito.ecommerce_api.dto.ProductResponse;
import com.Romito.ecommerce_api.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
class ProductControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private JsonMapper jsonMapper;

        @MockitoBean
        private ProductService productService;

    @Test
    void creaProductoDevuelve201() throws Exception {
        ProductRequest request = new ProductRequest(
                "Teclado mecánico", "Switches rojos",
                new BigDecimal("45000.00"), 10, 1L);

        ProductResponse response = new ProductResponse(
                1L, "Teclado mecánico", "Switches rojos",
                new BigDecimal("45000.00"), 10, 1L, "Periféricos");

        when(productService.create(request)).thenReturn(response);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Teclado mecánico"))
                .andExpect(jsonPath("$.categoryName").value("Periféricos"));
    }

    @Test
    void creaProductoConNombreVacioDevuelve400() throws Exception {
        ProductRequest invalido = new ProductRequest(
                "", null, new BigDecimal("100.00"), 5, 1L);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
        void buscarProductoInexistenteLanzaExcepcionSinManejar() {
        when(productService.findById(999L))
                .thenThrow(new NoSuchElementException("Producto no encontrado: 999"));

        assertThrows(Exception.class, () ->
                mockMvc.perform(get("/api/products/999")));
        }
}