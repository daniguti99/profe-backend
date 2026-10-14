package com.daniel.profe.profe_backend.domain.port.in;

import com.daniel.profe.profe_backend.application.dto.session.PublicSessionDTO;

/**
 * Puerto de entrada para consultar el detalle de una sesión pública.
 *
 * <p>El identificador del usuario no viaja en la URL ni en el cuerpo: llega desde el token. Se usa
 * solo para decidir si la consulta genera una visualización: si quien consulta es el propio autor,
 * el contador no se incrementa (RF-11).</p>
 */
public interface GetPublicSessionDetailUseCase {

    /**
     * Devuelve el detalle de una sesión pública y activa.
     *
     * @param id     identificador de la sesión, tomado de la ruta
     * @param userId identificador del usuario tomado del token, nunca de la ruta ni del cuerpo
     * @return detalle de la sesión con los datos de su autor
     */
    PublicSessionDTO getPublicSession(Integer id, Long userId);
}
