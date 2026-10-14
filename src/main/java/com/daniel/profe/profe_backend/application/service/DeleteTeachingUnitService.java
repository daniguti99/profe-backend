package com.daniel.profe.profe_backend.application.service;

import com.daniel.profe.profe_backend.application.dto.teachingunit.DeleteTeachingUnitResponse;
import com.daniel.profe.profe_backend.application.dto.teachingunit.TeachingUnitDTO;
import com.daniel.profe.profe_backend.domain.exception.TeachingUnitNotOwnedException;
import com.daniel.profe.profe_backend.domain.exception.TeachingUnitNotFoundException;
import com.daniel.profe.profe_backend.domain.model.Session;
import com.daniel.profe.profe_backend.domain.model.TeachingUnit;
import com.daniel.profe.profe_backend.domain.port.in.DeleteTeachingUnitUseCase;
import com.daniel.profe.profe_backend.domain.port.out.SessionRepositoryPort;
import com.daniel.profe.profe_backend.domain.port.out.TeachingUnitRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Caso de uso para eliminar (desactivar) una unidad didáctica.
 *
 * <p>El servicio valida que la unidad existe, pertenece al usuario autenticado y está activa.
 * Luego marca la unidad como inactiva ({@code active=false}) y desactiva en cascada todas sus
 * sesiones activas. No se borra físicamente nada.
 *
 * <p>La operación es transaccional: si algo falla durante la cascada, no se desactiva nada.
 *
 * <p>El servicio es una función pura de {@code id} y {@code userId}: no lee el contexto de seguridad
 * ({@code SecurityContextHolder}), no conoce el transporte y no decide el formato de la respuesta
 * HTTP. Solo orquesta los puertos de salida y traduce de dominio a DTO.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class DeleteTeachingUnitService implements DeleteTeachingUnitUseCase {

    private final TeachingUnitRepositoryPort teachingUnitRepositoryPort;
    private final SessionRepositoryPort sessionRepositoryPort;

    @Override
    public DeleteTeachingUnitResponse deleteTeachingUnit(Integer id, Long userId) {
        TeachingUnit unit = teachingUnitRepositoryPort.findById(id)
                .orElseThrow(() -> new TeachingUnitNotFoundException(
                        "La unidad didáctica con id " + id + " no existe"));

        if (!unit.getUserId().equals(userId.intValue())) {
            throw new TeachingUnitNotOwnedException("La unidad didáctica no te pertenece");
        }

        if (!Boolean.TRUE.equals(unit.getActive())) {
            throw new TeachingUnitNotFoundException(
                    "La unidad didáctica con id " + id + " no existe");
        }

        unit.setActive(false);
        TeachingUnit savedUnit = teachingUnitRepositoryPort.save(unit);

        List<Session> sessions = sessionRepositoryPort.findByTeachingUnitId(id);
        for (Session session : sessions) {
            session.setActive(false);
            sessionRepositoryPort.save(session);
        }

        return DeleteTeachingUnitResponse.builder()
                .message("Unidad didáctica eliminada correctamente")
                .teachingUnit(TeachingUnitDTO.builder()
                        .id(savedUnit.getId())
                        .userId(savedUnit.getUserId())
                        .courseId(savedUnit.getCourseId())
                        .title(savedUnit.getTitle())
                        .description(savedUnit.getDescription())
                        .schedule(savedUnit.getSchedule())
                        .notes(savedUnit.getNotes())
                        .active(savedUnit.getActive())
                        .build())
                .build();
    }
}
