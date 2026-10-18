package com.daniel.profe.profe_backend.application.service;

import com.daniel.profe.profe_backend.application.dto.session.SessionDTO;
import com.daniel.profe.profe_backend.application.dto.session.UpdateVisibilityRequest;
import com.daniel.profe.profe_backend.application.dto.session.UpdateVisibilityResponse;
import com.daniel.profe.profe_backend.domain.exception.SessionNotOwnedException;
import com.daniel.profe.profe_backend.domain.exception.SessionNotFoundException;
import com.daniel.profe.profe_backend.domain.model.Session;
import com.daniel.profe.profe_backend.domain.port.in.UpdateSessionVisibilityUseCase;
import com.daniel.profe.profe_backend.domain.port.out.SessionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Caso de uso para cambiar la visibilidad de una sesión (publicar o volver a privada).
 *
 * <p>El servicio valida que la sesión existe, pertenece al usuario autenticado y está activa,
 * siguiendo el mismo orden que la edición y el borrado: existencia (404), propiedad (403) y
 * actividad (404). Después fija la visibilidad y, solo al publicar, la fecha de publicación.</p>
 *
 * <p>Es una función pura de {@code id} y {@code userId}: no lee el contexto de seguridad
 * ({@code SecurityContextHolder}), no conoce el transporte y no decide el formato de la respuesta
 * HTTP. Solo orquesta el puerto de salida y traduce de dominio a DTO.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UpdateSessionVisibilityService implements UpdateSessionVisibilityUseCase {

    private final SessionRepositoryPort sessionRepositoryPort;

    @Override
    public UpdateVisibilityResponse updateVisibility(Integer id, UpdateVisibilityRequest request, Long userId) {
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

        boolean publicar = Boolean.TRUE.equals(request.getIsPublic());
        session.setIsPublic(publicar);
        if (publicar) {
            // Al publicar (o volver a publicar) se fija la fecha actual. Al despublicar no se toca:
            // se conserva la última fecha de publicación, tal como exige RF-3.
            session.setPublishedAt(LocalDateTime.now());
        }

        Session saved = sessionRepositoryPort.save(session);

        return UpdateVisibilityResponse.builder()
                .message("Visibilidad de la sesión actualizada correctamente")
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
                        .warmUpObservations(saved.getWarmUpObservations())
                        .mainPartTime(saved.getMainPartTime())
                        .mainPartDescription(saved.getMainPartDescription())
                        .mainPartObservations(saved.getMainPartObservations())
                        .coolDownTime(saved.getCoolDownTime())
                        .coolDownDescription(saved.getCoolDownDescription())
                        .coolDownObservations(saved.getCoolDownObservations())
                        .active(saved.getActive())
                        .isPublic(saved.getIsPublic())
                        .publishedAt(saved.getPublishedAt())
                        .build())
                .build();
    }
}
