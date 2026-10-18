package com.daniel.profe.profe_backend.application.service;

import com.daniel.profe.profe_backend.application.dto.auth.RegisterRequest;
import com.daniel.profe.profe_backend.application.dto.auth.RegisterResponse;
import com.daniel.profe.profe_backend.domain.exception.EmailAlreadyExistsException;
import com.daniel.profe.profe_backend.domain.exception.EmailMismatchException;
import com.daniel.profe.profe_backend.domain.exception.UsernameAlreadyExistsException;
import com.daniel.profe.profe_backend.domain.exception.ValidationException;
import com.daniel.profe.profe_backend.domain.model.FotoPerfil;
import com.daniel.profe.profe_backend.domain.model.User;
import com.daniel.profe.profe_backend.domain.model.UserRole;
import com.daniel.profe.profe_backend.domain.port.in.RegisterUseCase;
import com.daniel.profe.profe_backend.domain.port.out.PasswordEncoderPort;
import com.daniel.profe.profe_backend.domain.port.out.PhotoStoragePort;
import com.daniel.profe.profe_backend.domain.port.out.UserRepositoryPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class RegisterService implements RegisterUseCase {

    /** Tipos de imagen admitidos para la foto de perfil. */
    private static final Set<String> FOTO_TIPOS_PERMITIDOS =
            Set.of("image/jpeg", "image/png", "image/webp");

    /** Tamaño máximo de la foto: 2 MB, igual que el validado en el frontend. */
    private static final long FOTO_TAMANO_MAXIMO = 2L * 1024 * 1024;

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final PhotoStoragePort photoStoragePort;

    @Override
    public RegisterResponse register(RegisterRequest registerRequest, FotoPerfil foto) {

        // Validar el nombre de usuario según las reglas definidas
        validateUsername(registerRequest);

        // Validar si el nombre de usuario ya existe en la base de datos
        existsByUsername(registerRequest);

        // Validar si el email o el nombre de usuario ya existen en la base de datos
        existsByEmail(registerRequest);

        // Validar si los emails coinciden
        validateEmails(registerRequest);

        // Validar si las contraseñas coinciden
        validatePasswords(registerRequest);

        // Subir la foto elegida (opcional) y quedarnos con su URL pública
        String fotoUrl = subirFoto(registerRequest, foto);

        // Construir el objeto User a partir del RegisterRequest
        User user = buildUser(registerRequest, fotoUrl);

        // Guardar el usuario en la base de datos. Si falla con la foto ya subida,
        // se intenta eliminar el activo para no dejar huérfanos en la nube.
        User savedUser;
        try {
            savedUser = userRepositoryPort.save(user);
        } catch (RuntimeException e) {
            if (fotoUrl != null) {
                photoStoragePort.eliminarFoto(nombrePublicoFoto(registerRequest));
            }
            throw e;
        }

        // Construir la respuesta de registro a partir del usuario guardado
        return buildResponse(savedUser);
    }

    private void existsByEmail(RegisterRequest registerRequest) {
        if(userRepositoryPort.existsByEmail(registerRequest.getEmail())) {
            throw new EmailAlreadyExistsException("El email introducido ya existe");
        }
    }

    private void existsByUsername(RegisterRequest registerRequest) {
        if(userRepositoryPort.existsByUsername(registerRequest.getUsername())) {
            throw new UsernameAlreadyExistsException("El nombre de usuario introducido ya existe");
        }
    }

    private void validateEmails(RegisterRequest registerRequest) {
        if(!registerRequest.getEmail().equals(registerRequest.getConfirmEmail())) {
            throw new EmailMismatchException("Los emails introducidos no coinciden");
        }
    }

    private void validatePasswords(RegisterRequest registerRequest) {
        if(!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new EmailMismatchException("Las contraseñas introducidas no coinciden");
        }

        if (registerRequest.getPassword().length() < 8) {
            throw new ValidationException(
                    "La contraseña debe tener al menos 8 caracteres"
            );
        }

        if (!registerRequest.getPassword()
                .matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")) {
            throw new ValidationException(
                    "La contraseña debe contener al menos una letra mayúscula, una letra minúscula y un número"
            );
        }
    }

    /**
     * Valida la foto elegida, la sube al almacenamiento externo y devuelve su URL.
     * Devuelve {@code null} cuando no se ha puesto ninguna foto.
     */
    private String subirFoto(RegisterRequest registerRequest, FotoPerfil foto) {
        if (foto == null) {
            return null;
        }

        validateFoto(foto);

        return photoStoragePort.subirFoto(
                foto.getContenido(),
                foto.getContentType(),
                nombrePublicoFoto(registerRequest));
    }

    /** La foto solo se sube si pasa estas reglas; si no, se responde 400 en español. */
    private void validateFoto(FotoPerfil foto) {
        if (foto.getContenido() == null || foto.getContenido().length == 0) {
            throw new ValidationException("La foto de perfil está vacía");
        }

        if (foto.getContentType() == null
                || !FOTO_TIPOS_PERMITIDOS.contains(foto.getContentType())) {
            throw new ValidationException(
                    "La foto de perfil debe ser una imagen JPG, PNG o WEBP");
        }

        if (foto.getContenido().length > FOTO_TAMANO_MAXIMO) {
            throw new ValidationException("La foto de perfil no puede superar 2 MB");
        }
    }

    /** Identificador único de la foto en el almacenamiento: el usuario es único en la base. */
    private String nombrePublicoFoto(RegisterRequest registerRequest) {
        return "usuario_" + registerRequest.getUsername();
    }

    private User buildUser(RegisterRequest registerRequest, String fotoUrl) {
        return User.builder()
                .username(registerRequest.getUsername())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoderPort.encode(registerRequest.getPassword()))
                .role(UserRole.ROLE_USER)
                .province(registerRequest.getProvince())
                .locality(registerRequest.getLocality())
                .photo(fotoUrl)
                .build();
    }

    private RegisterResponse buildResponse(User user) {
        return RegisterResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .photo(user.getPhoto())
                .message("Usuario registrado correctamente")
                .build();
    }

    private void validateUsername(RegisterRequest request) {

        if (request.getUsername().length() < 4) {
            throw new ValidationException(
                    "El nombre de usuario debe tener al menos 4 caracteres"
            );
        }

        if (request.getUsername().length() > 20) {
            throw new ValidationException(
                    "El nombre de usuario no puede tener más de 20 caracteres"
            );
        }

        if (!request.getUsername().matches("^[a-zA-Z0-9_]+$")) {
            throw new ValidationException(
                    "El nombre de usuario solo puede contener letras, números y guiones bajos"
            );
        }
    }


}
