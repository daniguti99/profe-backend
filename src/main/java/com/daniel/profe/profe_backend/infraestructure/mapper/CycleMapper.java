package com.daniel.profe.profe_backend.infraestructure.mapper;

import com.daniel.profe.profe_backend.domain.model.Cycle;
import com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.entity.CycleEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CycleMapper {
    Cycle toDomain(CycleEntity cycleEntity);
    CycleEntity toEntity(Cycle cycle);
}
