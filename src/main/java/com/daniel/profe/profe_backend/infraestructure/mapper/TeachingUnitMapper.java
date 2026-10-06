package com.daniel.profe.profe_backend.infraestructure.mapper;

import com.daniel.profe.profe_backend.domain.model.TeachingUnit;
import com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.entity.TeachingUnitEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Traduce una entidad de persistencia al modelo de dominio.
 *
 * <p>Precondición de {@link #toDomain(TeachingUnitEntity)}: solo se invoca con entidades
 * devueltas por la consulta con {@code JOIN FETCH} de {@code user} y {@code course} (la crea la
 * tarea T4, en {@code TeachingUnitJpaRepository}). Ese inner join garantiza que ambas relaciones
 * llegan cargadas y no pueden ser nulas, que es justo lo que necesita el DTO (RF-4).</p>
 *
 * <p>Qué pasa si se rompe la precondición: la implementación generada por MapStruct no desreferencia
 * a ciegas, sino que envuelve la lectura en comprobaciones
 * ({@code entity.getUser()} / {@code entity.getCourse()} y {@code if (user == null) return null}),
 * de modo que una relación nula no produce un {@code NullPointerException} sino un
 * {@code userId} o {@code courseId} a {@code null}, silenciosamente. Eso tampoco es aceptable,
 * porque el DTO garantiza que ambos identificadores viajan siempre informs (RF-4). Por eso la
 * garantía no se busca aquí —un mapper mapea, no filtra (D4)— sino en la consulta con
 * {@code JOIN FETCH} interno (D3) y, como red de seguridad, en el filtro defensivo del servicio
 * (D4).</p>
 *
 * <p>Solo {@code toDomain}: esta historia es de lectura, así que no se crea {@code toEntity}, que
 * además obligaría a ignorar {@code user}, {@code course}, {@code sessions} y
 * {@code teachingUnitTags}. Se añadirá cuando llegue el CRUD de unidades.</p>
 */
@Mapper(componentModel = "spring")
public interface TeachingUnitMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "courseId", source = "course.id")
    TeachingUnit toDomain(TeachingUnitEntity teachingUnitEntity);
}
