package com.daniel.profe.profe_backend.infraestructure.mapper;

import com.daniel.profe.profe_backend.domain.model.Session;
import com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.entity.SessionEntity;
import com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.entity.TeachingUnitEntity;
import com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface SessionMapper {

    @Mapping(target = "teachingUnitId", source = "teachingUnit.id")
    @Mapping(target = "userId", source = "user.id")
    Session toDomain(SessionEntity sessionEntity);

    @Mapping(target = "teachingUnit.id", source = "teachingUnitId")
    @Mapping(target = "user.id", source = "userId")
    SessionEntity toEntity(Session session);
}
