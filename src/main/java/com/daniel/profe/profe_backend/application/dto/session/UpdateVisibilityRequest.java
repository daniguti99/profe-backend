package com.daniel.profe.profe_backend.application.dto.session;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO de la petición de cambio de visibilidad de una sesión.
 *
 * <p>Solo transporta el nuevo estado: {@code true} para publicar y {@code false} para volver a
 * privada. Es obligatorio, de modo que una petición sin el campo devuelve 400.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateVisibilityRequest {

    @NotNull(message = "La visibilidad es obligatoria")
    private Boolean isPublic;
}
