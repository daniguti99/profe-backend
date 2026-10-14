package com.daniel.profe.profe_backend.application.service;

import com.daniel.profe.profe_backend.application.dto.session.SessionDTO;
import com.daniel.profe.profe_backend.application.dto.session.UpdateSessionRequest;
import com.daniel.profe.profe_backend.application.dto.session.UpdateSessionResponse;
import com.daniel.profe.profe_backend.domain.exception.SessionNotOwnedException;
import com.daniel.profe.profe_backend.domain.exception.SessionNotFoundException;
import com.daniel.profe.profe_backend.domain.model.Session;
import com.daniel.profe.profe_backend.domain.port.in.UpdateSessionUseCase;
import com.daniel.profe.profe_backend.domain.port.out.SessionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso para actualizar parcialmente una sesión existente (PATCH).
 *
 * <p>El servicio valida que la sesión existe, pertenece al usuario autenticado y está activa.
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
public class UpdateSessionService implements UpdateSessionUseCase {

    private final SessionRepositoryPort sessionRepositoryPort;

    @Override
    public UpdateSessionResponse updateSession(Integer id, UpdateSessionRequest request, Long userId) {
        Session session = sessionRepositoryPort.findById(id)
                .orElseThrow(() -> new SessionNotFoundException(
                        "La sesión con id " + id + " no existe"));

        if (!session.getUserId().equals(userId.intValue())) {
            throw new SessionNotOwnedException("La sesión no te pertenece");
        }

        if (!Boolean.TRUE.equals(session.getActive())) {
            throw new SessionNotFoundException(
                    "La sesión con id " + id + " no existe");
        }

        if (request.getTitle() != null) {
            session.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            session.setDescription(request.getDescription());
        }
        if (request.getMaterials() != null) {
            session.setMaterials(request.getMaterials());
        }
        if (request.getTotalDuration() != null) {
            session.setTotalDuration(request.getTotalDuration());
        }
        if (request.getDate() != null) {
            session.setDate(request.getDate());
        }
        if (request.getWarmUpTime() != null) {
            session.setWarmUpTime(request.getWarmUpTime());
        }
        if (request.getWarmUpDescription() != null) {
            session.setWarmUpDescription(request.getWarmUpDescription());
        }
        if (request.getWarmUpGraphicUrl() != null) {
            session.setWarmUpGraphicUrl(request.getWarmUpGraphicUrl());
        }
        if (request.getWarmUpObservations() != null) {
            session.setWarmUpObservations(request.getWarmUpObservations());
        }
        if (request.getMainPartTime() != null) {
            session.setMainPartTime(request.getMainPartTime());
        }
        if (request.getMainPartDescription() != null) {
            session.setMainPartDescription(request.getMainPartDescription());
        }
        if (request.getMainPartGraphicUrl() != null) {
            session.setMainPartGraphicUrl(request.getMainPartGraphicUrl());
        }
        if (request.getMainPartObservations() != null) {
            session.setMainPartObservations(request.getMainPartObservations());
        }
        if (request.getCoolDownTime() != null) {
            session.setCoolDownTime(request.getCoolDownTime());
        }
        if (request.getCoolDownDescription() != null) {
            session.setCoolDownDescription(request.getCoolDownDescription());
        }
        if (request.getCoolDownGraphicUrl() != null) {
            session.setCoolDownGraphicUrl(request.getCoolDownGraphicUrl());
        }
        if (request.getCoolDownObservations() != null) {
            session.setCoolDownObservations(request.getCoolDownObservations());
        }

        Session saved = sessionRepositoryPort.save(session);

        return UpdateSessionResponse.builder()
                .message("Sesión actualizada correctamente")
                .session(SessionDTO.builder()
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
                        .build())
                .build();
    }
}
