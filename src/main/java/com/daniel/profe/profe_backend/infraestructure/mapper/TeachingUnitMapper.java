package com.daniel.profe.profe_backend.infraestructure.mapper;

import com.daniel.profe.profe_backend.domain.model.TeachingUnit;
import com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.entity.TeachingUnitEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface TeachingUnitMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "courseId", source = "course.id")
    TeachingUnit toDomain(TeachingUnitEntity teachingUnitEntity);

    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "course.id", source = "courseId")
    TeachingUnitEntity toEntity(TeachingUnit teachingUnit);
}
