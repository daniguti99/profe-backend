package com.daniel.profe.profe_backend.application.dto.session;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de un elemento del listado de sesiones públicas.
 *
 * <p>Es un resumen: los datos que bastan para pintar una tarjeta en la pantalla de exploración
 * (título, descripción, fecha, contadores y autor). El contenido completo se obtiene en el detalle,
 * {@link PublicSessionDTO}.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicSessionSummaryDTO {

    private Integer id;
    private String title;
    private String description;
    private LocalDate date;
    private LocalDateTime publishedAt;
    private Integer views;
    private Integer downloads;

    private AuthorDTO author;
}
