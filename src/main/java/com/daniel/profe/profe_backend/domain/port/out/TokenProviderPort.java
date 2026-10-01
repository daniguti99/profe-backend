package com.daniel.profe.profe_backend.domain.port.out;

import com.daniel.profe.profe_backend.domain.model.User;

public interface TokenProviderPort {
    String generateToken(User user);
    long getExpirationMs();
    User validateToken(String token);
}
