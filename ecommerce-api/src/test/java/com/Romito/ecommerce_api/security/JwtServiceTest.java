package com.Romito.ecommerce_api.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret",
                "claveDePruebaBienLargaParaElTestDeJwtServiceOk123");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 3600000L);
    }

    @Test
    void generaUnTokenYExtraeUsernameYRolCorrectamente() {
        String token = jwtService.generateToken("jromito", "USER");

        assertEquals("jromito", jwtService.extractUsername(token));
        assertEquals("USER", jwtService.extractRole(token));
    }

    @Test
    void elTokenEsValidoParaElUsernameCorrecto() {
        String token = jwtService.generateToken("jromito", "USER");

        assertTrue(jwtService.isTokenValid(token, "jromito"));
        assertFalse(jwtService.isTokenValid(token, "otroUsuario"));
    }

    @Test
    void unTokenYaExpiradoNoEsValido() {
        ReflectionTestUtils.setField(jwtService, "expirationMs", -1000L);
        String token = jwtService.generateToken("jromito", "USER");

        assertThrows(Exception.class, () -> jwtService.isTokenValid(token, "jromito"));
    }
}