package com.daniel.profe.profe_backend.application.dto.session;

import lombok.*;

import java.util.List;

/**
 * DTO de envoltura de la respuesta del listado de sesiones públicas.
 *
 * <p>Envuelve siempre la página en un objeto con las claves {@code sessions} y {@code message}, de
 * modo que el array plano es estructuralmente imposible: incluso sin resultados, la respuesta sigue
 * siendo un objeto. Añade además los metadatos de paginación {@code page}, {@code size} y
 * {@code hasMore}, que el cliente usa para pedir la página siguiente (scroll infinito).</p>
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

    /** Página devuelta (base 0). */
    private int page;

    /** Tamaño de página aplicado. */
    private int size;

    /** {@code true} si quedan más sesiones por devolver. */
    private boolean hasMore;

    /**
     * Construye la envoltura a partir de la página devuelta por el caso de uso y de los parámetros
     * de paginación solicitados.
     *
     * <p>El mensaje se elige según haya o no sesiones, conservando el comportamiento de la spec
     * 012. Si la página no trae sesiones, {@code hasMore} se fuerza a {@code false} para no dejar
     * al cliente pidiendo una página que no existe.</p>
     */
    public static PublicSessionListResponseDTO dePagina(
            PublicSessionPageDTO pagina, int page, int size) {
        List<PublicSessionSummaryDTO> sessions =
                pagina.getSessions() == null ? List.of() : pagina.getSessions();

        return PublicSessionListResponseDTO.builder()
                .sessions(sessions)
                .message(sessions.isEmpty() ? MENSAJE_SIN_SESIONES : MENSAJE_CON_SESIONES)
                .page(page)
                .size(size)
                .hasMore(!sessions.isEmpty() && pagina.isHasMore())
                .build();
    }
}
