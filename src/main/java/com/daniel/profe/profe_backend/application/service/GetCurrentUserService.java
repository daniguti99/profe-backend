package com.daniel.profe.profe_backend.application.service;

import com.daniel.profe.profe_backend.application.dto.auth.UserInfo;
import com.daniel.profe.profe_backend.domain.model.User;
import com.daniel.profe.profe_backend.domain.port.in.GetCurrentUserUseCase;
import com.daniel.profe.profe_backend.domain.port.out.TokenProviderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GetCurrentUserService implements GetCurrentUserUseCase {

    private final TokenProviderPort tokenProvider;

    @Override
    public UserInfo getCurrentUser(String token) {
        User user = tokenProvider.validateToken(token);

        return UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
