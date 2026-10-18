package com.daniel.profe.profe_backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Session {

    private Integer id;

    private Integer teachingUnitId;
    private Integer userId;

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

    private Boolean active;

    /** Visibilidad de la sesión: {@code true} si es pública, {@code false} si es privada. */
    private Boolean isPublic;

    /** Fecha y hora de la última publicación; {@code null} si nunca se ha publicado. */
    private LocalDateTime publishedAt;

    /** Número de visualizaciones acumuladas de la sesión pública. */
    private Integer views;

    /** Número de descargas acumuladas; se incrementará con la exportación a PDF. */
    private Integer downloads;
}