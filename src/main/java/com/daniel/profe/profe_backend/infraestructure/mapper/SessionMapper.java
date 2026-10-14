package com.daniel.profe.profe_backend.infraestructure.mapper;

import com.daniel.profe.profe_backend.domain.model.PublicSession;
import com.daniel.profe.profe_backend.domain.model.Session;
import com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.entity.SessionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SessionMapper {

    @Mapping(target = "teachingUnitId", source = "teachingUnit.id")
    @Mapping(target = "userId", source = "user.id")
    Session toDomain(SessionEntity sessionEntity);

    @Mapping(target = "teachingUnit.id", source = "teachingUnitId")
    @Mapping(target = "user.id", source = "userId")
    SessionEntity toEntity(Session session);

    /**
     * Traduce la entidad con la relación {@code user} cargada al modelo de lectura
     * {@link PublicSession}, extrayendo del autor su identificador, nombre de usuario y foto.
     *
     * <p>El mapeo de las tres propiedades del autor se hace con MapStruct, que genera una guarda
     * nula: si {@code user} fuera nulo, las tres saldrían a {@code null} en silencio. La garantía
     * real de que nunca es nulo está en el {@code JOIN FETCH} interno de las consultas públicas;
     * esto es la red de seguridad.</p>
     */
    @Mapping(target = "teachingUnitId", source = "teachingUnit.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "authorId", source = "user.id")
    @Mapping(target = "authorUsername", source = "user.username")
    @Mapping(target = "authorPhoto", source = "user.photo")
    PublicSession toPublicDomain(SessionEntity sessionEntity);
}
