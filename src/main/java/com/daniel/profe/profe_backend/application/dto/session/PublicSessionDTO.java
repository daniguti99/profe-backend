package com.daniel.profe.profe_backend.application.dto.session;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO del detalle completo de una sesión pública.
 *
 * <p>Incluye todos los campos de la sesión más {@code isPublic}, {@code publishedAt}, los
 * contadores y el autor. Es el mismo detalle que ve el propietario, más los datos públicos del
 * autor.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicSessionDTO {

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
    private Boolean isPublic;
    private LocalDateTime publishedAt;
    private Integer views;
    private Integer downloads;

    private AuthorDTO author;
}
