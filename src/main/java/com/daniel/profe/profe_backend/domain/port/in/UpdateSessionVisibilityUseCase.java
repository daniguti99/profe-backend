package com.daniel.profe.profe_backend.domain.port.in;

import com.daniel.profe.profe_backend.application.dto.session.UpdateVisibilityRequest;
import com.daniel.profe.profe_backend.application.dto.session.UpdateVisibilityResponse;

/**
 * Puerto de entrada para cambiar la visibilidad de una sesión (publicar o volver a privada).
 *
 * <p>El identificador del usuario no viaja en la URL ni en el cuerpo: llega desde el token. El
 * adaptador de entrada (el controlador REST) lo extrae del {@code Authentication} que deja
 * preparado el filtro de JWT y lo pasa como argumento, de modo que el caso de uso no puede
 * publicar ni despublicar la sesión de otro usuario.</p>
 *
 * <p>Sobre los tipos del identificador: {@code userId} es {@code Long} porque es el tipo de la
 * identidad del token ({@code User.id}), mientras que el puerto de salida usa {@code Integer}; la
 * conversión se realiza en el servicio.</p>
 */
public interface UpdateSessionVisibilityUseCase {

    /**
     * Cambia la visibilidad de la sesión indicada.
     *
     * @param id      identificador de la sesión, tomado de la ruta
     * @param request cuerpo con el nuevo estado de visibilidad
     * @param userId  identificador del usuario tomado del token, nunca de la ruta ni del cuerpo
     * @return respuesta con la sesión actualizada y su mensaje
     */
    UpdateVisibilityResponse updateVisibility(Integer id, UpdateVisibilityRequest request, Long userId);
}
