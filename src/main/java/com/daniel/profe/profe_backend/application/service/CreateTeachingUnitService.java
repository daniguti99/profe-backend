package com.daniel.profe.profe_backend.application.service;

import com.daniel.profe.profe_backend.application.dto.teachingunit.CreateTeachingUnitRequest;
import com.daniel.profe.profe_backend.application.dto.teachingunit.CreateTeachingUnitResponse;
import com.daniel.profe.profe_backend.domain.exception.CourseNotFoundException;
import com.daniel.profe.profe_backend.domain.model.TeachingUnit;
import com.daniel.profe.profe_backend.domain.port.in.CreateTeachingUnitUseCase;
import com.daniel.profe.profe_backend.domain.port.out.CourseRepositoryPort;
import com.daniel.profe.profe_backend.domain.port.out.TeachingUnitRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateTeachingUnitService implements CreateTeachingUnitUseCase {

    private final TeachingUnitRepositoryPort teachingUnitRepositoryPort;
    private final CourseRepositoryPort courseRepositoryPort;

    @Override
    public CreateTeachingUnitResponse createTeachingUnit(CreateTeachingUnitRequest request, Long userId) {
        if (!courseRepositoryPort.existsById(request.getCourseId())) {
            throw new CourseNotFoundException("El curso con id " + request.getCourseId() + " no existe");
        }

        TeachingUnit unit = TeachingUnit.builder()
                .userId(userId.intValue())
                .courseId(request.getCourseId())
                .active(true)
                .title(request.getTitle())
                .description(request.getDescription())
                .schedule(request.getSchedule())
                .notes(request.getNotes())
                .build();

        TeachingUnit saved = teachingUnitRepositoryPort.save(unit);

        return CreateTeachingUnitResponse.builder()
                .id(saved.getId())
                .userId(saved.getUserId())
                .courseId(saved.getCourseId())
                .title(saved.getTitle())
                .description(saved.getDescription())
                .schedule(saved.getSchedule())
                .notes(saved.getNotes())
                .active(saved.getActive())
                .build();
    }
}
