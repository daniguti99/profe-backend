package com.daniel.profe.profe_backend.application.dto.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Página de resúmenes de sesiones públicas devuelta por el caso de uso.
 *
 * <p>Es el equivalente de aplicación del modelo de dominio {@link
 * com.daniel.profe.profe_backend.domain.model.PublicSessionPage}: transporta los resúmenes ya
 * mapeados ({@link PublicSessionSummaryDTO}) junto con {@code hasMore}. El adaptador de entrada lo
 * envuelve en {@link PublicSessionListResponseDTO} añadiendo la página y el tamaño solicitados.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicSessionPageDTO {

    /** Resúmenes de la página; nunca {@code null}. */
    private List<PublicSessionSummaryDTO> sessions;

    /** {@code true} si existe al menos una sesión más allá de las devueltas. */
    private boolean hasMore;
}
