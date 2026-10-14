package com.daniel.profe.profe_backend.application.dto.session;

import lombok.*;

import java.util.List;

/**
 * DTO de envoltura de la respuesta del listado de sesiones públicas.
 *
 * <p>Envuelve siempre la lista en un objeto con las claves {@code sessions} y {@code message}, de
 * modo que el array plano es estructuralmente imposible: incluso sin resultados, la respuesta sigue
 * siendo un objeto.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicSessionListResponseDTO {

    /** Mensaje devuelto cuando hay al menos una sesión pública. */
    public static final String MENSAJE_CON_SESIONES = "Se han encontrado sesiones públicas";

    /** Mensaje devuelto cuando no hay ninguna sesión pública. */
    public static final String MENSAJE_SIN_SESIONES = "No se han encontrado sesiones públicas";

    private List<PublicSessionSummaryDTO> sessions;
    private String message;

    /**
     * Factoría para el caso con sesiones: devuelve la lista recibida, sin reordenar ni copiar, junto
     * al mensaje de sesiones públicas encontradas.
     */
    public static PublicSessionListResponseDTO conSesiones(List<PublicSessionSummaryDTO> sessions) {
        return PublicSessionListResponseDTO.builder()
                .sessions(sessions)
                .message(MENSAJE_CON_SESIONES)
                .build();
    }

    /**
     * Factoría para el caso sin sesiones: devuelve una lista vacía (nunca {@code null}) junto al
     * mensaje de sesiones públicas no encontradas.
     */
    public static PublicSessionListResponseDTO sinSesiones() {
        return PublicSessionListResponseDTO.builder()
                .sessions(List.of())
                .message(MENSAJE_SIN_SESIONES)
                .build();
    }
}
