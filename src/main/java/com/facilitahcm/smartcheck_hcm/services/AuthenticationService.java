package com.facilitahcm.smartcheck_hcm.services;

import com.facilitahcm.smartcheck_hcm.dtos.LoginRequestDTO;
import com.facilitahcm.smartcheck_hcm.dtos.RegisterRequestDTO;
import com.facilitahcm.smartcheck_hcm.dtos.RegisterResponseDTO;
import com.facilitahcm.smartcheck_hcm.exceptions.UsernameExistsException;
import com.facilitahcm.smartcheck_hcm.models.Users;
import com.facilitahcm.smartcheck_hcm.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UsersRepository usersRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationService(AuthenticationManager authenticationManager, UsersRepository usersRepository, TokenService tokenService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.usersRepository = usersRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    public String realizarLogin(LoginRequestDTO loginRequestDTO) {
        try {
            UsernamePasswordAuthenticationToken userNamePassword = new UsernamePasswordAuthenticationToken(loginRequestDTO.login(), loginRequestDTO.password());
            var auth = authenticationManager.authenticate(userNamePassword);

            String token = tokenService.generateToken((Users) auth.getPrincipal());

            return token;
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(loginRequestDTO.login());
        }
    }

    public RegisterResponseDTO cadastrarUsuario(RegisterRequestDTO registerRequestDTO) {
        if (usersRepository.findByLogin(registerRequestDTO.login()).isPresent()) {
            throw new UsernameExistsException("Usuário com esse login já existe: " + registerRequestDTO.login());
        }

        String senhaHash = passwordEncoder.encode(registerRequestDTO.password());
        Users user = new Users(registerRequestDTO.login(), senhaHash, registerRequestDTO.tipoUsuario());

        usersRepository.save(user);

        return new RegisterResponseDTO(user.getUsername(), user.getTipoUsuario());
    }



}
