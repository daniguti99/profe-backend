package com.daniel.profe.profe_backend.domain.port.in;

import com.daniel.profe.profe_backend.application.dto.session.PublicSessionPageDTO;
import com.daniel.profe.profe_backend.domain.model.PublicSessionSort;

/**
 * Puerto de entrada para consultar el catálogo de sesiones públicas.
 *
 * <p>No recibe el usuario: el listado es el mismo para todos los usuarios autenticados. Solo
 * transporta el criterio de ordenación (RF-6), que es un enum de dominio y no un detalle del
 * transporte, y los parámetros de paginación (página base 0 y tamaño).</p>
 */
public interface GetPublicSessionsUseCase {

    /**
     * Devuelve una página de resúmenes de sesiones públicas y activas, en el orden indicado.
     *
     * @param sort criterio de ordenación; nunca nulo
     * @param page página solicitada (base 0); nunca negativa
     * @param size número de sesiones por página (1..50)
     * @return página con los resúmenes y si quedan más por devolver; sin sesiones si la página está
     *         más allá del final
     */
    PublicSessionPageDTO getPublicSessions(PublicSessionSort sort, int page, int size);
}
