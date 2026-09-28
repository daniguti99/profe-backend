package com.daniel.profe.profe_backend.application.service;

import com.daniel.profe.profe_backend.application.dto.auth.LoginRequest;
import com.daniel.profe.profe_backend.application.dto.auth.LoginResponse;
import com.daniel.profe.profe_backend.application.dto.auth.UserInfo;
import com.daniel.profe.profe_backend.domain.exception.PasswordMismatchException;
import com.daniel.profe.profe_backend.domain.exception.UserNotFoundException;
import com.daniel.profe.profe_backend.domain.model.User;
import com.daniel.profe.profe_backend.domain.port.in.LoginUseCase;
import com.daniel.profe.profe_backend.domain.port.out.PasswordEncoderPort;
import com.daniel.profe.profe_backend.domain.port.out.TokenProviderPort;
import com.daniel.profe.profe_backend.domain.port.out.UserRepositoryPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class LoginService implements LoginUseCase {

    private final UserRepositoryPort userRepository;
    private final TokenProviderPort tokenProvider;
    private final PasswordEncoderPort passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        //Verificar que el usuario existe
        User user = validateUserExists(loginRequest);

        //Verificar que la contraseña es correcta
        validatePasswords(loginRequest, user);

        String token = tokenProvider.generateToken(user);

        UserInfo userInfo = UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();

        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresInMs(tokenProvider.getExpirationMs())
                .user(userInfo)
                .build();
    }

    public void validatePasswords(LoginRequest loginRequest, User user) {
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new PasswordMismatchException("Contraseña incorrecta");
        }
    }

    public User validateUserExists(LoginRequest loginRequest) {
        return userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new UserNotFoundException("Mensaje de error: Usuario no encontrado"));
    }
}
