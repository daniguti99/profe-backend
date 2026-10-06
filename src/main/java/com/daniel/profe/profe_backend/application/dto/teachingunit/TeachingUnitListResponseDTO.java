package com.daniel.profe.profe_backend.application.dto.teachingunit;

import lombok.*;

import java.util.List;

/**
 * DTO de envoltura de la respuesta de unidades didácticas.
 *
 * <p>Envuelve siempre la lista de unidades en un objeto con las claves {@code teachingUnits} y
 * {@code message}, de forma que el array plano es estructuralmente imposible (RF-3 y requisito no
 * funcional de US-018): incluso sin resultados, la respuesta sigue siendo un objeto.
 *
 * <p>Los dos textos fijos de la respuesta viven únicamente en las constantes
 * {@link #MENSAJE_CON_UNIDADES} y {@link #MENSAJE_SIN_UNIDADES}, y las factorías
 * {@link #conUnidades(List)} y {@link #sinUnidades()} son funciones puras que garantizan una lista
 * nunca nula. No lleva anotaciones de Jackson a propósito: ni {@code @JsonInclude} ni
 * {@code @JsonIgnore} pueden alterar las dos claves exigidas por la spec.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeachingUnitListResponseDTO {

    /** Mensaje devuelto cuando el usuario tiene al menos una unidad didáctica (RF-6). */
    public static final String MENSAJE_CON_UNIDADES = "Se han encontrado unidades didácticas";

    /** Mensaje devuelto cuando el usuario no tiene ninguna unidad didáctica (RF-5). */
    public static final String MENSAJE_SIN_UNIDADES = "No se han encontrado unidades didácticas";

    private List<TeachingUnitDTO> teachingUnits;
    private String message;

    /**
     * Factoría para el caso con unidades: devuelve la lista recibida, sin reordenar ni copiar, junto
     * al mensaje de unidades encontradas.
     *
     * @param teachingUnits lista de unidades del usuario autenticado
     * @return envoltura con la lista recibida y el mensaje de unidades encontradas
     */
    public static TeachingUnitListResponseDTO conUnidades(List<TeachingUnitDTO> teachingUnits) {
        return TeachingUnitListResponseDTO.builder()
                .teachingUnits(teachingUnits)
                .message(MENSAJE_CON_UNIDADES)
                .build();
    }

    /**
     * Factoría para el caso sin unidades: devuelve una lista vacía (nunca {@code null}) junto al
     * mensaje de unidades no encontradas.
     *
     * @return envoltura con lista vacía y el mensaje de unidades no encontradas
     */
    public static TeachingUnitListResponseDTO sinUnidades() {
        return TeachingUnitListResponseDTO.builder()
                .teachingUnits(List.of())
                .message(MENSAJE_SIN_UNIDADES)
                .build();
    }
}