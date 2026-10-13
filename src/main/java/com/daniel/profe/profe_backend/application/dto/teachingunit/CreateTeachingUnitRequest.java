package com.daniel.profe.profe_backend.application.dto.teachingunit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTeachingUnitRequest {

    @NotNull(message = "El curso es obligatorio")
    private Integer courseId;

    @NotBlank(message = "El título es obligatorio")
    private String title;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @NotBlank(message = "La temporalización es obligatoria")
    private String schedule;

    @NotBlank(message = "Las notas son obligatorias")
    private String notes;
}
