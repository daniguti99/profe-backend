package com.daniel.profe.profe_backend.application.dto.session;

import lombok.*;

/**
 * DTO de la respuesta del cambio de visibilidad.
 *
 * <p>Devuelve la sesión actualizada (con su {@code isPublic} y {@code publishedAt} ya coherentes) y
 * un mensaje, siguiendo el patrón de envoltura del resto de respuestas del proyecto.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateVisibilityResponse {

    private String message;
    private SessionDTO session;
}
