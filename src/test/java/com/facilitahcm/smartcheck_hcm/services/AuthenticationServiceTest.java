package com.facilitahcm.smartcheck_hcm.services;

import com.facilitahcm.smartcheck_hcm.dtos.LoginRequestDTO;
import com.facilitahcm.smartcheck_hcm.dtos.RegisterRequestDTO;
import com.facilitahcm.smartcheck_hcm.dtos.RegisterResponseDTO;
import com.facilitahcm.smartcheck_hcm.enums.TipoUsuario;
import com.facilitahcm.smartcheck_hcm.exceptions.UsernameExistsException;
import com.facilitahcm.smartcheck_hcm.models.Users;
import com.facilitahcm.smartcheck_hcm.repositories.UsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void deveRetornarToken_quandoLoginForValido() {
        LoginRequestDTO request = new LoginRequestDTO("joao", "123456");
        Users user = criarUsuario("joao", TipoUsuario.ADMIN);
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(tokenService.generateToken(user)).thenReturn("jwt-token");

        String jwt = authenticationService.realizarLogin(request);

        assertEquals("jwt-token", jwt);
        verify(authenticationManager).authenticate(argThat(authenticationToken ->
                "joao".equals(authenticationToken.getName()) && "123456".equals(authenticationToken.getCredentials())
        ));
        verify(tokenService).generateToken(user);
    }

    @Test
    void deveLancarBadCredentialsException_quandoLoginForInvalido() {
        LoginRequestDTO request = new LoginRequestDTO("joao", "senha-errada");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("bad credentials"));

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authenticationService.realizarLogin(request)
        );

        assertEquals("joao", exception.getMessage());
        verify(tokenService, never()).generateToken(any());
    }

    @Test
    void deveCadastrarUsuario_quandoLoginNaoExistir() {
        RegisterRequestDTO request = new RegisterRequestDTO("joao", "123456", TipoUsuario.EMPLOYEE);

        when(usersRepository.findByLogin("joao")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123456")).thenReturn("hash-da-senha");
        when(usersRepository.save(any(Users.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RegisterResponseDTO response = authenticationService.cadastrarUsuario(request);

        assertEquals("joao", response.login());
        assertEquals(TipoUsuario.EMPLOYEE, response.tipoUsuario());

        ArgumentCaptor<Users> userCaptor = ArgumentCaptor.forClass(Users.class);
        verify(usersRepository).save(userCaptor.capture());

        Users salvo = userCaptor.getValue();
        assertEquals("joao", salvo.getLogin());
        assertEquals("hash-da-senha", salvo.getPassword());
        assertEquals(TipoUsuario.EMPLOYEE, salvo.getTipoUsuario());
        verify(passwordEncoder).encode("123456");
    }

    @Test
    void deveLancarUsernameExistsException_quandoLoginJaExistir() {
        RegisterRequestDTO request = new RegisterRequestDTO("joao", "123456", TipoUsuario.EMPLOYEE);
        Users usuarioExistente = criarUsuario("joao", TipoUsuario.ADMIN);

        when(usersRepository.findByLogin("joao")).thenReturn(Optional.of(usuarioExistente));

        UsernameExistsException exception = assertThrows(
                UsernameExistsException.class,
                () -> authenticationService.cadastrarUsuario(request)
        );

        assertTrue(exception.getMessage().contains("joao"));
        verify(usersRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    private Users criarUsuario(String login, TipoUsuario tipoUsuario) {
        return Users.builder()
                .id(1L)
                .login(login)
                .password("hash")
                .tipoUsuario(tipoUsuario)
                .build();
    }
}


