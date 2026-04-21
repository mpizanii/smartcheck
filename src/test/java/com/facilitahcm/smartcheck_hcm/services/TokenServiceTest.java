package com.facilitahcm.smartcheck_hcm.services;

import com.facilitahcm.smartcheck_hcm.enums.TipoUsuario;
import com.facilitahcm.smartcheck_hcm.models.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "jwtSecret", "teste-secret-key-123456789");
    }

    @Test
    void deveGerarEValidarToken_quandoUsuarioForValido() {
        Users user = Users.builder()
                .id(1L)
                .login("joao")
                .password("hash")
                .tipoUsuario(TipoUsuario.ADMIN)
                .build();

        String token = tokenService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals("joao", tokenService.validateToken(token));
    }

    @Test
    void deveRetornarStringVazia_quandoTokenForInvalido() {
        String subject = tokenService.validateToken("token-invalido");

        assertEquals("", subject);
    }
}

