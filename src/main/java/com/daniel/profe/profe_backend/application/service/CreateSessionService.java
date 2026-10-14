package com.daniel.profe.profe_backend.application.service;

import com.daniel.profe.profe_backend.application.dto.session.CreateSessionRequest;
import com.daniel.profe.profe_backend.application.dto.session.CreateSessionResponse;
import com.daniel.profe.profe_backend.domain.exception.TeachingUnitNotOwnedException;
import com.daniel.profe.profe_backend.domain.exception.TeachingUnitNotFoundException;
import com.daniel.profe.profe_backend.domain.model.Session;
import com.daniel.profe.profe_backend.domain.model.TeachingUnit;
import com.daniel.profe.profe_backend.domain.port.in.CreateSessionUseCase;
import com.daniel.profe.profe_backend.domain.port.out.SessionRepositoryPort;
import com.daniel.profe.profe_backend.domain.port.out.TeachingUnitRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateSessionService implements CreateSessionUseCase {

    private final SessionRepositoryPort sessionRepositoryPort;
    private final TeachingUnitRepositoryPort teachingUnitRepositoryPort;

    @Override
    public CreateSessionResponse createSession(CreateSessionRequest request, Long userId) {
        List<TeachingUnit> userUnits = teachingUnitRepositoryPort.findByUserId(userId.intValue());

        TeachingUnit unit = userUnits.stream()
                .filter(u -> u.getId().equals(request.getTeachingUnitId()))
                .findFirst()
                .orElseThrow(() -> new TeachingUnitNotFoundException(
                        "La unidad didáctica con id " + request.getTeachingUnitId() + " no existe"));

        if (!unit.getUserId().equals(userId.intValue())) {
            throw new TeachingUnitNotOwnedException("La unidad didáctica no te pertenece");
        }

        Session session = Session.builder()
                .teachingUnitId(request.getTeachingUnitId())
                .userId(userId.intValue())
                .active(true)
                .title(request.getTitle())
                .description(request.getDescription())
                .materials(request.getMaterials())
                .totalDuration(request.getTotalDuration())
                .date(request.getDate())
                .warmUpTime(request.getWarmUpTime())
                .warmUpDescription(request.getWarmUpDescription())
                .warmUpGraphicUrl(request.getWarmUpGraphicUrl())
                .warmUpObservations(request.getWarmUpObservations())
                .mainPartTime(request.getMainPartTime())
                .mainPartDescription(request.getMainPartDescription())
                .mainPartGraphicUrl(request.getMainPartGraphicUrl())
                .mainPartObservations(request.getMainPartObservations())
                .coolDownTime(request.getCoolDownTime())
                .coolDownDescription(request.getCoolDownDescription())
                .coolDownGraphicUrl(request.getCoolDownGraphicUrl())
                .coolDownObservations(request.getCoolDownObservations())
                .build();

        Session saved = sessionRepositoryPort.save(session);

        return CreateSessionResponse.builder()
                .id(saved.getId())
                .teachingUnitId(saved.getTeachingUnitId())
                .userId(saved.getUserId())
                .title(saved.getTitle())
                .description(saved.getDescription())
                .materials(saved.getMaterials())
                .totalDuration(saved.getTotalDuration())
                .date(saved.getDate())
                .warmUpTime(saved.getWarmUpTime())
                .warmUpDescription(saved.getWarmUpDescription())
                .warmUpGraphicUrl(saved.getWarmUpGraphicUrl())
                .warmUpObservations(saved.getWarmUpObservations())
                .mainPartTime(saved.getMainPartTime())
                .mainPartDescription(saved.getMainPartDescription())
                .mainPartGraphicUrl(saved.getMainPartGraphicUrl())
                .mainPartObservations(saved.getMainPartObservations())
                .coolDownTime(saved.getCoolDownTime())
                .coolDownDescription(saved.getCoolDownDescription())
                .coolDownGraphicUrl(saved.getCoolDownGraphicUrl())
                .coolDownObservations(saved.getCoolDownObservations())
                .active(saved.getActive())
                .build();
    }
}
