package com.daniel.profe.profe_backend.domain.port.in;

import com.daniel.profe.profe_backend.application.dto.teachingunit.DeleteTeachingUnitResponse;

/**
 * Puerto de entrada para eliminar (desactivar) una unidad didáctica.
 *
 * <p>El identificador del usuario no viaja en la URL ni en el cuerpo de la petición: llega desde el
 * token. El adaptador de entrada (el controlador REST) lo extrae del {@code Authentication} que deja
 * preparado el filtro de JWT y lo pasa como argumento. De este modo el caso de uso solo puede
 * eliminar las unidades del usuario indicado y nunca las de otro.
 *
 * <p>La eliminación es un soft delete con cascada: al desactivar la unidad, todas sus sesiones
 * activas también se desactivan. La operación es transaccional.
 *
 * <p>Sobre los tipos del identificador: {@code userId} es {@code Long} porque es el tipo de la
 * identidad del token ({@code User.id}), mientras que el puerto de salida
 * {@code TeachingUnitRepositoryPort.findById} usa {@code Integer} porque es el tipo de
 * {@code TeachingUnit.id} y de la columna {@code id_unidad}. La conversión entre ambos tipos ocurre
 * en el servicio, que es la única pieza que conoce los dos extremos.
 */
public interface DeleteTeachingUnitUseCase {
    DeleteTeachingUnitResponse deleteTeachingUnit(Integer id, Long userId);
}
