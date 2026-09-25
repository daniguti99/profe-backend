package com.daniel.profe.profe_backend.application.service;

import com.daniel.profe.profe_backend.application.dto.auth.RegisterRequest;
import com.daniel.profe.profe_backend.application.dto.auth.RegisterResponse;
import com.daniel.profe.profe_backend.domain.exception.EmailAlreadyExistsException;
import com.daniel.profe.profe_backend.domain.exception.UsernameAlreadyExistsException;
import com.daniel.profe.profe_backend.domain.model.User;
import com.daniel.profe.profe_backend.domain.port.in.RegisterUseCase;
import com.daniel.profe.profe_backend.domain.port.out.PasswordEncoderPort;
import com.daniel.profe.profe_backend.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterService implements RegisterUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {

        if(userRepositoryPort.existsByEmail(registerRequest.getEmail())) {
            throw new EmailAlreadyExistsException("El email introducido ya existe");
        }

        if(userRepositoryPort.existsByUsername(registerRequest.getUsername())) {
            throw new UsernameAlreadyExistsException("El nombre de usuario introducido ya existe");
        }

        /*User user = User.builder()
                .username(registerRequest.getUsername())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(registerRequest.getPassword())
*/

        return null;
    }
}
