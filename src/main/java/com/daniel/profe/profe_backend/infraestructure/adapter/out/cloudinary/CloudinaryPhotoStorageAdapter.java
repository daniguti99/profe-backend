package com.daniel.profe.profe_backend.infraestructure.adapter.out.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.daniel.profe.profe_backend.domain.exception.PhotoStorageException;
import com.daniel.profe.profe_backend.domain.model.FotoPerfil;
import com.daniel.profe.profe_backend.domain.port.out.PhotoStoragePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Adaptador que guarda la foto de perfil en Cloudinary.
 *
 * <p>La imagen se sube original y se guarda la URL de entrega ya transformada a
 * avatar (400×400), de modo que el activo original queda en la nube por si más
 * adelante se necesita otra derivación.
 */
@Slf4j
@Component
public class CloudinaryPhotoStorageAdapter implements PhotoStoragePort {

    private static final String CARPETA = "profe/avatares";
    private static final String RECURSO = "image";

    private final Cloudinary cloudinary;

    public CloudinaryPhotoStorageAdapter(@Value("${cloudinary.url}") String cloudinaryUrl) {
        // El constructor valida la URL: si las credenciales faltan o son inválidas,
        // el backend no arranca en lugar de fallar en el primer registro con foto.
        this.cloudinary = new Cloudinary(cloudinaryUrl);
    }

    @Override
    @SuppressWarnings("unchecked")
    public String subirFoto(byte[] contenido, String contentType, String nombrePublico) {
        Path temporal = null;
        try {
            temporal = Files.createTempFile("profe-foto-", extension(contentType));
            Files.write(temporal, contenido);

            Map<String, Object> opciones = ObjectUtils.asMap(
                    "folder", CARPETA,
                    "public_id", nombrePublico,
                    "overwrite", true,
                    "resource_type", RECURSO);

            Map<String, Object> resultado =
                    cloudinary.uploader().upload(temporal.toFile(), opciones);

            String publicId = (String) resultado.get("public_id");
            return urlAvatar(publicId);

        } catch (IOException | RuntimeException e) {
            log.error("No se pudo subir la foto a Cloudinary", e);
            throw new PhotoStorageException(
                    "No se pudo subir la foto al almacenamiento externo", e);
        } finally {
            borrarTemporal(temporal);
        }
    }

    @Override
    public void eliminarFoto(String nombrePublico) {
        try {
            cloudinary.uploader().destroy(
                    CARPETA + "/" + nombrePublico,
                    ObjectUtils.asMap("resource_type", RECURSO));
        } catch (IOException | RuntimeException e) {
            // Mejor esfuerzo: el objetivo es no dejar activos huérfanos cuando el
            // registro posterior a la subida ha fallado, pero un fallo aquí no debe
            // tapar el error original que ha provocado la limpieza.
            log.warn("No se pudo eliminar la foto '{}' de Cloudinary: {}",
                    nombrePublico, e.getMessage());
        }
    }

    /** URL de entrega de la imagen ya recortada a avatar y optimizada. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private String urlAvatar(String publicId) {
        Transformation avatar = new Transformation()
                .width(400)
                .height(400)
                .crop("fill")
                .fetchFormat("auto")
                .quality("auto");

        return cloudinary.url().transformation(avatar).generate(publicId);
    }

    /** Extensión de archivo a partir del tipo MIME, para el fichero temporal. */
    private String extension(String contentType) {
        if (contentType == null) {
            return ".bin";
        }
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".bin";
        };
    }

    private void borrarTemporal(Path temporal) {
        if (temporal == null) {
            return;
        }
        try {
            Files.deleteIfExists(temporal);
        } catch (IOException e) {
            log.warn("No se pudo borrar el fichero temporal '{}': {}",
                    temporal.getFileName(), e.getMessage());
        }
    }
}
