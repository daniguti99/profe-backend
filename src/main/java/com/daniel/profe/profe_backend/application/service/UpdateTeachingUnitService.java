package com.daniel.profe.profe_backend.application.service;

import com.daniel.profe.profe_backend.application.dto.teachingunit.TeachingUnitDTO;
import com.daniel.profe.profe_backend.application.dto.teachingunit.UpdateTeachingUnitRequest;
import com.daniel.profe.profe_backend.application.dto.teachingunit.UpdateTeachingUnitResponse;
import com.daniel.profe.profe_backend.domain.exception.TeachingUnitNotOwnedException;
import com.daniel.profe.profe_backend.domain.exception.TeachingUnitNotFoundException;
import com.daniel.profe.profe_backend.domain.model.TeachingUnit;
import com.daniel.profe.profe_backend.domain.port.in.UpdateTeachingUnitUseCase;
import com.daniel.profe.profe_backend.domain.port.out.TeachingUnitRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso para actualizar parcialmente una unidad didáctica existente (PATCH).
 *
 * <p>El servicio valida que la unidad existe, pertenece al usuario autenticado y está activa.
 * Luego actualiza únicamente los campos presentes en la petición (los no nulos) y persiste.
 * Los campos ausentes (null) se dejan intactos.</p>
 *
 * <p>El servicio es una función pura de {@code id} y {@code userId}: no lee el contexto de seguridad
 * ({@code SecurityContextHolder}), no conoce el transporte y no decide el formato de la respuesta
 * HTTP. Solo orquesta el puerto de salida y traduce de dominio a DTO.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UpdateTeachingUnitService implements UpdateTeachingUnitUseCase {

    private final TeachingUnitRepositoryPort teachingUnitRepositoryPort;

    @Override
    public UpdateTeachingUnitResponse updateTeachingUnit(Integer id, UpdateTeachingUnitRequest request, Long userId) {
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

        if (request.getTitle() != null) {
            unit.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            unit.setDescription(request.getDescription());
        }
        if (request.getSchedule() != null) {
            unit.setSchedule(request.getSchedule());
        }
        if (request.getNotes() != null) {
            unit.setNotes(request.getNotes());
        }

        TeachingUnit saved = teachingUnitRepositoryPort.save(unit);

        return UpdateTeachingUnitResponse.builder()
                .message("Unidad didáctica actualizada correctamente")
                .teachingUnit(TeachingUnitDTO.builder()
                        .id(saved.getId())
                        .userId(saved.getUserId())
                        .courseId(saved.getCourseId())
                        .title(saved.getTitle())
                        .description(saved.getDescription())
                        .schedule(saved.getSchedule())
                        .notes(saved.getNotes())
                        .active(saved.getActive())
                        .build())
                .build();
    }
}
