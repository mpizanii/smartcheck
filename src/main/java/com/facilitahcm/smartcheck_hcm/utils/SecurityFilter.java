package com.facilitahcm.smartcheck_hcm.utils;

import com.facilitahcm.smartcheck_hcm.repositories.UsersRepository;
import com.facilitahcm.smartcheck_hcm.services.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final UsersRepository usersRepository;

    public SecurityFilter(TokenService tokenService, UsersRepository usersRepository) {
        this.tokenService = tokenService;
        this.usersRepository = usersRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        var token = recoverToken(request);

        if (token != null) {
            String login = tokenService.validateToken(token);

            // Se token inválido ou sem subject não autentica
            if (login != null && !login.isBlank()) {
                Optional<UserDetails> user = usersRepository.findByLogin(login);

                // Só cria authentication se o usuário existir
                user.ifPresent(userDetails -> {
                    var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                });
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(@NonNull HttpServletRequest request) {
        String authheader = request.getHeader("Authorization");
        if (authheader == null || !authheader.startsWith("Bearer ")) {
            return null;
        }

        return authheader.substring(7);
    }
}
