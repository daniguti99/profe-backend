package com.daniel.profe.profe_backend.application.service;

import com.daniel.profe.profe_backend.application.dto.session.DeleteSessionResponse;
import com.daniel.profe.profe_backend.application.dto.session.SessionDTO;
import com.daniel.profe.profe_backend.domain.exception.SessionNotOwnedException;
import com.daniel.profe.profe_backend.domain.exception.SessionNotFoundException;
import com.daniel.profe.profe_backend.domain.model.Session;
import com.daniel.profe.profe_backend.domain.port.in.DeleteSessionUseCase;
import com.daniel.profe.profe_backend.domain.port.out.SessionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso para eliminar (desactivar) una sesión.
 *
 * <p>El servicio valida que la sesión existe, pertenece al usuario autenticado y está activa.
 * Luego marca la sesión como inactiva ({@code active=false}) y persiste. No se borra físicamente.
 *
 * <p>El servicio es una función pura de {@code id} y {@code userId}: no lee el contexto de seguridad
 * ({@code SecurityContextHolder}), no conoce el transporte y no decide el formato de la respuesta
 * HTTP. Solo orquesta el puerto de salida y traduce de dominio a DTO.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class DeleteSessionService implements DeleteSessionUseCase {

    private final SessionRepositoryPort sessionRepositoryPort;

    @Override
    public DeleteSessionResponse deleteSession(Integer id, Long userId) {
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

        session.setActive(false);
        Session saved = sessionRepositoryPort.save(session);

        return DeleteSessionResponse.builder()
                .message("Sesión eliminada correctamente")
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
                        .isPublic(saved.getIsPublic())
                        .publishedAt(saved.getPublishedAt())
                        .build())
                .build();
    }
}
