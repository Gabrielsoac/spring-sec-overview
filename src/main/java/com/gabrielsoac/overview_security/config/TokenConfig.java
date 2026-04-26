package com.gabrielsoac.overview_security.config;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.gabrielsoac.overview_security.entity.User;

@Component
public class TokenConfig {

    private String secret = "secret";

    public String generateToken(User user){
        return JWT.create()
            .withClaim("userId", user.getId())
            .withSubject(user.getEmail())
            .withExpiresAt(Instant.now().plusSeconds(86400))
            .withIssuedAt(Instant.now())
            .sign(Algorithm.HMAC256(secret));    
    }

    public Optional<JWTUserData> validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            DecodedJWT decode = JWT.require(algorithm).build().verify(token);
            return Optional.of(
                JWTUserData.builder()
                    .email(decode.getSubject())
                    .userId(decode.getClaim("userId").asLong())
                        .build());
        } catch (JWTDecodeException e){
            return Optional.empty();
        }
    }
}
