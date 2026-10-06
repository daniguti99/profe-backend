package com.daniel.profe.profe_backend.application.dto.session;

import lombok.*;

import java.util.List;

/**
 * DTO de envoltura de la respuesta de sesiones.
 *
 * <p>Envuelve siempre la lista de sesiones en un objeto con las claves {@code sessions} y
 * {@code message}, de forma que el array plano es estructuralmente imposible: incluso sin
 * resultados, la respuesta sigue siendo un objeto.
 *
 * <p>Los dos textos fijos de la respuesta viven únicamente en las constantes
 * {@link #MENSAJE_CON_SESIONES} y {@link #MENSAJE_SIN_SESIONES}, y las factorías
 * {@link #conSesiones(List)} y {@link #sinSesiones()} son funciones puras que garantizan una lista
 * nunca nula. No lleva anotaciones de Jackson a propósito: ni {@code @JsonInclude} ni
 * {@code @JsonIgnore} pueden alterar las dos claves exigidas por la spec.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionListResponseDTO {

    /** Mensaje devuelto cuando el usuario tiene al menos una sesión. */
    public static final String MENSAJE_CON_SESIONES = "Se han encontrado sesiones";

    /** Mensaje devuelto cuando el usuario no tiene ninguna sesión. */
    public static final String MENSAJE_SIN_SESIONES = "No se han encontrado sesiones";

    private List<SessionDTO> sessions;
    private String message;

    /**
     * Factoría para el caso con sesiones: devuelve la lista recibida, sin reordenar ni copiar, junto
     * al mensaje de sesiones encontradas.
     *
     * @param sessions lista de sesiones del usuario autenticado
     * @return envoltura con la lista recibida y el mensaje de sesiones encontradas
     */
    public static SessionListResponseDTO conSesiones(List<SessionDTO> sessions) {
        return SessionListResponseDTO.builder()
                .sessions(sessions)
                .message(MENSAJE_CON_SESIONES)
                .build();
    }

    /**
     * Factoría para el caso sin sesiones: devuelve una lista vacía (nunca {@code null}) junto al
     * mensaje de sesiones no encontradas.
     *
     * @return envoltura con lista vacía y el mensaje de sesiones no encontradas
     */
    public static SessionListResponseDTO sinSesiones() {
        return SessionListResponseDTO.builder()
                .sessions(List.of())
                .message(MENSAJE_SIN_SESIONES)
                .build();
    }
}
