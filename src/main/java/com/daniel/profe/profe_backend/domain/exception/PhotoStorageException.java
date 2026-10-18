package com.daniel.profe.profe_backend.domain.exception;

/**
 * Fallo al subir o eliminar la foto en el almacenamiento externo (Cloudinary).
 *
 * <p>Se separa de {@link ValidationException}: aquí el problema no es el archivo que
 * ha enviado el usuario, sino el servicio externo.
 */
public class PhotoStorageException extends RuntimeException {
    public PhotoStorageException(String message) {
        super(message);
    }

    public PhotoStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
