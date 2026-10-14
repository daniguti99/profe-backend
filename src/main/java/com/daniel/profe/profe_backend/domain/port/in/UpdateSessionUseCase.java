package com.daniel.profe.profe_backend.domain.port.in;

import com.daniel.profe.profe_backend.application.dto.session.UpdateSessionRequest;
import com.daniel.profe.profe_backend.application.dto.session.UpdateSessionResponse;

/**
 * Puerto de entrada para actualizar una sesión.
 *
 * <p>El identificador del usuario no viaja en la URL ni en el cuerpo de la petición: llega desde el
 * token. El adaptador de entrada (el controlador REST) lo extrae del {@code Authentication} que deja
 * preparado el filtro de JWT y lo pasa como argumento. De este modo el caso de uso solo puede
 * actualizar las sesiones del usuario indicado y nunca las de otro.
 *
 * <p>Sobre los tipos del identificador: {@code userId} es {@code Long} porque es el tipo de la
 * identidad del token ({@code User.id}), mientras que el puerto de salida
 * {@code SessionRepositoryPort.findById} usa {@code Integer} porque es el tipo de
 * {@code Session.id} y de la columna {@code id_sesion}. La conversión entre ambos tipos ocurre en
 * el servicio, que es la única pieza que conoce los dos extremos.
 */
public interface UpdateSessionUseCase {
    UpdateSessionResponse updateSession(Integer id, UpdateSessionRequest request, Long userId);
}
