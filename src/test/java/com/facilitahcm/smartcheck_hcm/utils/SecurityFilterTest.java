package com.facilitahcm.smartcheck_hcm.utils;

import com.facilitahcm.smartcheck_hcm.enums.TipoUsuario;
import com.facilitahcm.smartcheck_hcm.models.Users;
import com.facilitahcm.smartcheck_hcm.repositories.UsersRepository;
import com.facilitahcm.smartcheck_hcm.services.TokenService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityFilterTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private SecurityFilter securityFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void naoDeveAutenticar_quandoNaoHouverHeaderAuthorization() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        securityFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(tokenService, usersRepository);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void deveAutenticar_quandoTokenForValidoEUsuarioExistir() throws Exception {
        Users user = Users.builder()
                .id(1L)
                .login("joao")
                .password("hash")
                .tipoUsuario(TipoUsuario.ADMIN)
                .build();

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-valido");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(tokenService.validateToken("token-valido")).thenReturn("joao");
        when(usersRepository.findByLogin("joao")).thenReturn(Optional.of(user));

        securityFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertInstanceOf(UsernamePasswordAuthenticationToken.class, authentication);
        assertEquals(user, authentication.getPrincipal());
        assertEquals(user.getAuthorities(), authentication.getAuthorities());
        verify(tokenService).validateToken("token-valido");
        verify(usersRepository).findByLogin("joao");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void naoDeveAutenticar_quandoTokenForValidoMasUsuarioNaoExistir() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-valido");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(tokenService.validateToken("token-valido")).thenReturn("joao");
        when(usersRepository.findByLogin("joao")).thenReturn(Optional.empty());

        securityFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(tokenService).validateToken("token-valido");
        verify(usersRepository).findByLogin("joao");
        verify(filterChain).doFilter(request, response);
    }
}


