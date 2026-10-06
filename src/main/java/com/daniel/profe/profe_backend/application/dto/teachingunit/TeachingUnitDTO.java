package com.daniel.profe.profe_backend.application.dto.teachingunit;

import lombok.*;

/**
 * DTO de un elemento de la respuesta de unidades didácticas.
 *
 * <p>Declara exactamente los 7 campos del modelo de dominio {@code TeachingUnit}, con los mismos
 * tipos que este: identificadores {@code Integer} (como en {@code TeachingUnitEntity} y en las
 * columnas {@code id} e {@code id_usuario}) y textos {@code String}.
 *
 * <p>No lleva anotaciones de Jackson ni campos de validación a propósito: los campos de texto
 * pueden venir vacíos y deben viajar en el JSON como {@code null} (RF-4 de US-018).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeachingUnitDTO {
    private Integer id;
    private Integer userId;
    private Integer courseId;

    private String title;
    private String description;
    private String schedule;
    private String notes;
}