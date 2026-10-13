package com.daniel.profe.profe_backend.application.dto.session;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSessionRequest {

    @NotNull(message = "La unidad didáctica es obligatoria")
    private Integer teachingUnitId;

    @NotBlank(message = "El título es obligatorio")
    private String title;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @NotBlank(message = "Los materiales son obligatorios")
    private String materials;

    @NotBlank(message = "La duración total es obligatoria")
    private String totalDuration;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate date;

    @NotBlank(message = "El tiempo de calentamiento es obligatorio")
    private String warmUpTime;

    @NotBlank(message = "La descripción del calentamiento es obligatoria")
    private String warmUpDescription;

    private String warmUpGraphicUrl;

    @NotBlank(message = "Las observaciones del calentamiento son obligatorias")
    private String warmUpObservations;

    @NotBlank(message = "El tiempo de parte principal es obligatorio")
    private String mainPartTime;

    @NotBlank(message = "La descripción de parte principal es obligatoria")
    private String mainPartDescription;

    private String mainPartGraphicUrl;

    @NotBlank(message = "Las observaciones de parte principal son obligatorias")
    private String mainPartObservations;

    @NotBlank(message = "El tiempo de vuelta a la calma es obligatorio")
    private String coolDownTime;

    @NotBlank(message = "La descripción de vuelta a la calma es obligatoria")
    private String coolDownDescription;

    private String coolDownGraphicUrl;

    @NotBlank(message = "Las observaciones de vuelta a la calma son obligatorias")
    private String coolDownObservations;
}
