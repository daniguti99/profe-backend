package com.daniel.profe.profe_backend.infraestructure.security;

import com.daniel.profe.profe_backend.domain.port.out.PasswordEncoderPort;
import org.springframework.security.crypto.password.PasswordEncoder;

public class BCryptAdapter implements PasswordEncoderPort {

    private final PasswordEncoder passwordEncoder;

    public BCryptAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
