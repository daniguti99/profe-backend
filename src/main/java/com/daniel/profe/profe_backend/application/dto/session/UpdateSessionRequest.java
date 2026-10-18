package com.daniel.profe.profe_backend.application.dto.session;

import lombok.*;

import java.time.LocalDate;

/**
 * DTO de la petición de actualización parcial de una sesión (PATCH).
 *
 * <p>En una actualización parcial, cada campo presente en el cuerpo indica un cambio; un campo
 * ausente (null) significa "no tocar este campo". Por eso no se aplican validaciones de
 * obligatoriedad: todos los campos son opcionales en cada petición individual.</p>
 *
 * <p>Los campos no editables (id, teachingUnitId, userId) no están presentes en este DTO.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSessionRequest {

    private String title;
    private String description;
    private String materials;
    private String totalDuration;
    private LocalDate date;
    private String warmUpTime;
    private String warmUpDescription;
    private String warmUpObservations;
    private String mainPartTime;
    private String mainPartDescription;
    private String mainPartObservations;
    private String coolDownTime;
    private String coolDownDescription;
    private String coolDownObservations;
}
