package com.facilitahcm.smartcheck_hcm.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.facilitahcm.smartcheck_hcm.models.Users;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TokenService {
    @Value("${jwt.secret}")
    private String jwtSecret;

    public String generateToken(Users user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            String token = JWT.create()
                    .withIssuer("smartcheck-auth")
                    .withSubject(user.getUsername())
                    .withExpiresAt(getExpirationTime())
                    .sign(algorithm);

            return token;
        } catch (JWTCreationException e) {
            throw new JWTCreationException("Erro ao criar token JWT", e);
        }
    }

    public String validateToken(String token) {
        try{
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            return JWT.require(algorithm)
                    .withIssuer("smartcheck-auth")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            return "";
        }
    }

    private Instant getExpirationTime() {
        return Instant.now().plusSeconds(3600); // Token válido por 1 hora
    }
}
