package com.daniel.profe.profe_backend.infraestructure.mapper;

import com.daniel.profe.profe_backend.domain.model.Course;
import com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.entity.CourseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    @Mapping(target = "cycleId", source = "cycle.id")
    Course toDomain(CourseEntity courseEntity);

    @Mapping(target = "cycle", ignore = true)
    CourseEntity toEntity(Course course);
}
