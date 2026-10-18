package com.daniel.profe.profe_backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Foto de perfil elegida en el registro, ya separada del fichero original.
 *
 * <p>Es un value object de dominio: contiene solo datos (bytes, tipo MIME y nombre
 * original) y no conoce nada de {@code MultipartFile} ni de Cloudinary. La validación
 * de tipo y tamaño vive en el caso de uso; la subida, en un puerto de salida.
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class FotoPerfil {

    /** Contenido binario de la imagen tal y como la envió el navegador. */
    private byte[] contenido;

    /** Tipo MIME declarado por el navegador, p. ej. {@code image/jpeg}. */
    private String contentType;

    /** Nombre original del archivo, usado solo con fines informativos y de trazabilidad. */
    private String nombreOriginal;
}
