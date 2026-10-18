package com.daniel.profe.profe_backend.application.service;

import com.daniel.profe.profe_backend.application.dto.auth.RegisterRequest;
import com.daniel.profe.profe_backend.application.dto.auth.RegisterResponse;
import com.daniel.profe.profe_backend.domain.exception.PhotoStorageException;
import com.daniel.profe.profe_backend.domain.exception.ValidationException;
import com.daniel.profe.profe_backend.domain.model.FotoPerfil;
import com.daniel.profe.profe_backend.domain.model.User;
import com.daniel.profe.profe_backend.domain.port.out.PasswordEncoderPort;
import com.daniel.profe.profe_backend.domain.port.out.PhotoStoragePort;
import com.daniel.profe.profe_backend.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias del registro con foto de perfil, sin contexto de Spring: el servicio solo
 * depende de puertos de salida, así que basta con dobles en memoria.
 */
class RegisterServiceTest {

    /** Doble de test: guarda el usuario recibido y permite simular fallos de guardado. */
    private static class RepositorioFalso implements UserRepositoryPort {
        User guardado;
        boolean fallarAlGuardar;

        @Override
        public User save(User user) {
            if (fallarAlGuardar) {
                throw new RuntimeException("Fallo simulado al guardar");
            }
            this.guardado = user;
            return user;
        }

        @Override
        public Optional<User> findById(Long id) {
            return Optional.empty();
        }

        @Override
        public Optional<User> findByEmail(String email) {
            return Optional.empty();
        }

        @Override
        public Optional<User> findByUsername(String username) {
            return Optional.empty();
        }

        @Override
        public boolean existsByEmail(String email) {
            return false;
        }

        @Override
        public boolean existsByUsername(String username) {
            return false;
        }
    }

    /** Doble de test: codifica cualquier contraseña de forma reconocible. */
    private static class CodificadorFalso implements PasswordEncoderPort {
        @Override
        public String encode(String rawPassword) {
            return "codificado:" + rawPassword;
        }

        @Override
        public boolean matches(String rawPassword, String encodedPassword) {
            return ("codificado:" + rawPassword).equals(encodedPassword);
        }
    }

    /** Doble de test: registra las subidas y borrados, y devuelve una URL fija. */
    private static class AlmacenamientoFalso implements PhotoStoragePort {
        final String urlDevuelta =
                "https://res.cloudinary.com/demo/image/upload/c_fill,w_400,h_400,f_auto,q_auto/usuario_prueba1";
        int subidas;
        int borrados;
        String nombrePublicoRecibido;
        byte[] contenidoRecibido;
        boolean fallarAlSubir;

        @Override
        public String subirFoto(byte[] contenido, String contentType, String nombrePublico) {
            if (fallarAlSubir) {
                throw new PhotoStorageException("Fallo simulado de Cloudinary");
            }
            this.subidas++;
            this.contenidoRecibido = contenido;
            this.nombrePublicoRecibido = nombrePublico;
            return urlDevuelta;
        }

        @Override
        public void eliminarFoto(String nombrePublico) {
            this.borrados++;
        }
    }

    private final RepositorioFalso repositorio = new RepositorioFalso();
    private final AlmacenamientoFalso almacenamiento = new AlmacenamientoFalso();

    private final RegisterService servicio = new RegisterService(
            repositorio,
            new CodificadorFalso(),
            almacenamiento);

    private static RegisterRequest solicitud() {
        return RegisterRequest.builder()
                .username("prueba1")
                .firstName("Ana")
                .lastName("Torres Lara")
                .email("ana@correo.com")
                .confirmEmail("ana@correo.com")
                .password("Abcdef12")
                .confirmPassword("Abcdef12")
                .province("Madrid")
                .locality("Alcalá de Henares")
                .build();
    }

    private static FotoPerfil foto(String contentType, int tamano) {
        return FotoPerfil.builder()
                .contenido(new byte[tamano])
                .contentType(contentType)
                .nombreOriginal("foto.tmp")
                .build();
    }

    @Test
    void registroSinFotoNoSubeNadaYGuardaLaFotoANula() {
        RegisterResponse respuesta = servicio.register(solicitud(), null);

        assertEquals(0, almacenamiento.subidas);
        assertNull(repositorio.guardado.getPhoto());
        assertNull(respuesta.getPhoto());
        assertEquals("ana@correo.com", respuesta.getEmail());
    }

    @Test
    void registroConFotoValidaLaSubeYGuardaLaUrl() {
        FotoPerfil foto = foto("image/jpeg", 1024);

        RegisterResponse respuesta = servicio.register(solicitud(), foto);

        assertEquals(1, almacenamiento.subidas);
        assertEquals("usuario_prueba1", almacenamiento.nombrePublicoRecibido);
        assertEquals(almacenamiento.urlDevuelta, repositorio.guardado.getPhoto());
        assertEquals(almacenamiento.urlDevuelta, respuesta.getPhoto());
        assertTrue(Arrays.equals(foto.getContenido(), almacenamiento.contenidoRecibido));
    }

    @Test
    void tipoDeImagenNoPermitidoSeRechazaSinSubirNada() {
        FotoPerfil foto = foto("image/gif", 1024);

        ValidationException error = assertThrows(
                ValidationException.class,
                () -> servicio.register(solicitud(), foto));

        assertEquals("La foto de perfil debe ser una imagen JPG, PNG o WEBP", error.getMessage());
        assertEquals(0, almacenamiento.subidas);
        assertNull(repositorio.guardado);
    }

    @Test
    void archivoDemasiadoGrandeSeRechazaSinSubirNada() {
        FotoPerfil foto = foto("image/png", 2 * 1024 * 1024 + 1);

        ValidationException error = assertThrows(
                ValidationException.class,
                () -> servicio.register(solicitud(), foto));

        assertEquals("La foto de perfil no puede superar 2 MB", error.getMessage());
        assertEquals(0, almacenamiento.subidas);
        assertNull(repositorio.guardado);
    }

    @Test
    void falloAlSubirLaFotoImpideCrearElUsuario() {
        almacenamiento.fallarAlSubir = true;

        assertThrows(
                PhotoStorageException.class,
                () -> servicio.register(solicitud(), foto("image/webp", 2048)));

        assertNull(repositorio.guardado);
    }

    @Test
    void falloAlGuardarEliminaLaFotoYaSubida() {
        repositorio.fallarAlGuardar = true;

        assertThrows(
                RuntimeException.class,
                () -> servicio.register(solicitud(), foto("image/jpeg", 1024)));

        assertEquals(1, almacenamiento.subidas);
        assertEquals(1, almacenamiento.borrados);
    }
}
