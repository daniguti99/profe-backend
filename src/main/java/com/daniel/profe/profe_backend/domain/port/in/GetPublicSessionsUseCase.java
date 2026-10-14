package com.daniel.profe.profe_backend.domain.port.in;

import com.daniel.profe.profe_backend.application.dto.session.PublicSessionSummaryDTO;
import com.daniel.profe.profe_backend.domain.model.PublicSessionSort;

import java.util.List;

/**
 * Puerto de entrada para consultar el catálogo de sesiones públicas.
 *
 * <p>No recibe el usuario: el listado es el mismo para todos los usuarios autenticados. Solo
 * transporta el criterio de ordenación (RF-6), que es un enum de dominio y no un detalle del
 * transporte.</p>
 */
public interface GetPublicSessionsUseCase {

    /**
     * Devuelve el resumen de todas las sesiones públicas y activas, en el orden indicado.
     *
     * @param sort criterio de ordenación; nunca nulo
     * @return lista de resúmenes; vacía si no hay sesiones públicas
     */
    List<PublicSessionSummaryDTO> getPublicSessions(PublicSessionSort sort);
}
