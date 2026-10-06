package com.daniel.profe.profe_backend.application.dto.session;

import lombok.*;

import java.time.LocalDate;

/**
 * DTO de un elemento de la respuesta de sesiones.
 *
 * <p>Declara exactamente los 19 campos del modelo de dominio {@code Session}, con los mismos
 * tipos que este: identificadores {@code Integer}, fecha {@code LocalDate} y textos {@code String}.
 *
 * <p>No lleva anotaciones de Jackson ni campos de validación a propósito: los campos de texto
 * pueden venir vacíos y deben viajar en el JSON como {@code null}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionDTO {
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
    private String warmUpGraphicUrl;
    private String warmUpObservations;

    private String mainPartTime;
    private String mainPartDescription;
    private String mainPartGraphicUrl;
    private String mainPartObservations;

    private String coolDownTime;
    private String coolDownDescription;
    private String coolDownGraphicUrl;
    private String coolDownObservations;
}
