package com.daniel.profe.profe_backend.domain.port.out;

/**
 * Puerto de salida para guardar la foto de perfil en un almacenamiento externo.
 *
 * <p>El dominio no sabe si la imagen va a disco, a la base de datos o a la nube:
 * solo pide que se suba y que le devuelvan la URL pública desde la que servirla.
 */
public interface PhotoStoragePort {

    /**
     * Sube la foto y devuelve su URL pública de entrega.
     *
     * @param contenido      bytes de la imagen
     * @param contentType    tipo MIME de la imagen (p. ej. {@code image/jpeg})
     * @param nombrePublico  identificador con el que guardarla en el almacenamiento
     * @return URL pública desde la que se puede ver la imagen
     * @throws com.daniel.profe.profe_backend.domain.exception.PhotoStorageException
     *         si la subida falla por cualquier motivo
     */
    String subirFoto(byte[] contenido, String contentType, String nombrePublico);

    /**
     * Elimina la foto del almacenamiento. Se usa para deshacer una subida cuya
     * operación posterior (p. ej. guardar el usuario) ha fallado.
     *
     * @param nombrePublico identificador con el que se guardó la imagen
     */
    void eliminarFoto(String nombrePublico);
}
