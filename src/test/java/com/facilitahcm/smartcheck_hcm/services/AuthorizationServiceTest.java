package com.facilitahcm.smartcheck_hcm.services;

import com.facilitahcm.smartcheck_hcm.enums.TipoUsuario;
import com.facilitahcm.smartcheck_hcm.models.Users;
import com.facilitahcm.smartcheck_hcm.repositories.UsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private AuthorizationService authorizationService;

    @Test
    void deveRetornarUsuario_quandoLoginExistir() {
        Users user = Users.builder()
                .id(1L)
                .login("joao")
                .password("hash")
                .tipoUsuario(TipoUsuario.ADMIN)
                .build();

        when(usersRepository.findByLogin("joao")).thenReturn(Optional.of(user));

        UserDetails resultado = authorizationService.loadUserByUsername("joao");

        assertEquals(user, resultado);
        assertEquals("joao", resultado.getUsername());
    }

    @Test
    void deveLancarUsernameNotFoundException_quandoLoginNaoExistir() {
        when(usersRepository.findByLogin("inexistente")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> authorizationService.loadUserByUsername("inexistente")
        );

        assertTrue(exception.getMessage().contains("inexistente"));
    }
}

