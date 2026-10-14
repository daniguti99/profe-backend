package com.daniel.profe.profe_backend.application.dto.session;

import lombok.*;

/**
 * DTO de la respuesta del detalle de una sesión pública.
 *
 * <p>Envuelve la sesión en un objeto con un mensaje, siguiendo el patrón de respuesta del proyecto.
 * El texto fijo vive únicamente en la constante {@link #MENSAJE} y la factoría {@link #deSesion}
 * es una función pura que garantiza la coherencia entre ambos campos.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicSessionDetailResponseDTO {

    /** Mensaje devuelto cuando se encuentra la sesión pública. */
    public static final String MENSAJE = "Sesión pública encontrada";

    private String message;
    private PublicSessionDTO session;

    /** Factoría que envuelve la sesión con el mensaje fijo de sesión pública encontrada. */
    public static PublicSessionDetailResponseDTO deSesion(PublicSessionDTO session) {
        return PublicSessionDetailResponseDTO.builder()
                .message(MENSAJE)
                .session(session)
                .build();
    }
}
