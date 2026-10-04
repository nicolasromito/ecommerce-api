package com.Romito.ecommerce_api.controller;

import com.Romito.ecommerce_api.config.SecurityConfig;
import com.Romito.ecommerce_api.dto.*;
import com.Romito.ecommerce_api.model.OrderStatus;
import com.Romito.ecommerce_api.security.AppUserDetailsService;
import com.Romito.ecommerce_api.security.JwtService;
import com.Romito.ecommerce_api.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@Import(SecurityConfig.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AppUserDetailsService appUserDetailsService;

    @Test
    @WithMockUser(username = "comprador1")
    void creaUnaOrdenDevuelve201() throws Exception {
        OrderRequest request = new OrderRequest(List.of(new OrderItemRequest(1L, 2)));

        OrderItemResponse item = new OrderItemResponse(
                1L, "Mouse", 2, new BigDecimal("10000.00"), new BigDecimal("20000.00"));
        OrderResponse response = new OrderResponse(
                1L, new BigDecimal("20000.00"), OrderStatus.PENDING, LocalDateTime.now(), List.of(item));

        when(orderService.create("comprador1", request)).thenReturn(response);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.total").value(20000.00))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.items[0].productName").value("Mouse"));
    }

    @Test
    @WithMockUser(username = "comprador1")
    void creaUnaOrdenSinItemsDevuelve400() throws Exception {
        String jsonVacio = """
                {"items": []}
                """;

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonVacio))
                .andExpect(status().isBadRequest());
    }
}