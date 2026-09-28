package com.daniel.profe.profe_backend.infraestructure.security;

import com.daniel.profe.profe_backend.domain.model.User;
import com.daniel.profe.profe_backend.domain.port.out.TokenProviderPort;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;


@Component
public class JwtTokenProviderAdapter implements TokenProviderPort {

    private final SecretKey secret;
    private final long expirationMs;

    public JwtTokenProviderAdapter(@Value("${jwt.secret}") String secret, @Value("${jwt.expirationMs}") long expirationMs) {
        this.secret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expirationMs = expirationMs;
    }

    @Override
    public String generateToken(User user) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("email", user.getEmail())
                .claim("username", user.getUsername())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(secret)
                .compact();
    }

    @Override
    public long getExpirationMs() {
        return expirationMs;
    }
}
