package com.daniel.profe.profe_backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Modelo de lectura de una sesión pública.
 *
 * <p>A diferencia de {@link Session}, que es el modelo de escritura y solo conoce el
 * {@code userId} del autor, este modelo reúne lo que necesitan el listado y el detalle públicos:
 * los campos de la sesión más los datos básicos del autor ({@code authorId}, {@code authorUsername}
 * y {@code authorPhoto}). Se construye en el adaptador de persistencia a partir de la entidad con
 * la relación {@code user} ya cargada, de modo que el servicio y los DTOs nunca manejan entidades
 * JPA.</p>
 *
 * <p>Es un modelo de solo lectura: no se usa para crear ni modificar sesiones.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublicSession {

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

    private Boolean active;
    private Boolean isPublic;
    private LocalDateTime publishedAt;
    private Integer views;
    private Integer downloads;

    /** Identificador del autor de la sesión. */
    private Integer authorId;

    /** Nombre de usuario del autor. */
    private String authorUsername;

    /** Foto del autor; puede ser nula. */
    private String authorPhoto;
}
