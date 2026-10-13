package com.daniel.profe.profe_backend.domain.port.in;

import com.daniel.profe.profe_backend.application.dto.teachingunit.CreateTeachingUnitRequest;
import com.daniel.profe.profe_backend.application.dto.teachingunit.CreateTeachingUnitResponse;

public interface CreateTeachingUnitUseCase {
    CreateTeachingUnitResponse createTeachingUnit(CreateTeachingUnitRequest request, Long userId);
}
