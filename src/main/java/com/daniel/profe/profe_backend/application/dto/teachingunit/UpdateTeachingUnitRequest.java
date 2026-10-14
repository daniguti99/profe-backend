package com.daniel.profe.profe_backend.application.dto.teachingunit;

import lombok.*;

/**
 * DTO de la petición de actualización parcial de una unidad didáctica (PATCH).
 *
 * <p>En una actualización parcial, cada campo presente en el cuerpo indica un cambio; un campo
 * ausente (null) significa "no tocar este campo". Por eso no se aplican validaciones de
 * obligatoriedad: todos los campos son opcionales en cada petición individual.</p>
 *
 * <p>Los campos no editables (id, courseId, userId) no están presentes en este DTO.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTeachingUnitRequest {

    private String title;
    private String description;
    private String schedule;
    private String notes;
}
