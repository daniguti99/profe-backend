package com.daniel.profe.profe_backend.application.service;

import com.daniel.profe.profe_backend.application.dto.session.AuthorDTO;
import com.daniel.profe.profe_backend.application.dto.session.PublicSessionDTO;
import com.daniel.profe.profe_backend.domain.exception.SessionNotFoundException;
import com.daniel.profe.profe_backend.domain.model.PublicSession;
import com.daniel.profe.profe_backend.domain.port.in.GetPublicSessionDetailUseCase;
import com.daniel.profe.profe_backend.domain.port.out.SessionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso para obtener el detalle de una sesión pública.
 *
 * <p>El servicio recibe el {@code userId} del token como dato y lo usa solo para decidir si la
 * consulta genera una visualización: si quien consulta es el propio autor, el contador no se
 * incrementa (RF-11). No lee el contexto de seguridad ni conoce el transporte.</p>
 *
 * <p>El filtro {@code publico = true AND activo = true} de la consulta hace que "no existe", "es
 * privada" y "está inactiva" se resuelvan como un único resultado vacío, que aquí se traduce en un
 * 404 unificado: el detalle público nunca revela la existencia de sesiones privadas ajenas.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional
public class GetPublicSessionDetailService implements GetPublicSessionDetailUseCase {

    private final SessionRepositoryPort sessionRepositoryPort;

    @Override
    public PublicSessionDTO getPublicSession(Integer id, Long userId) {
        PublicSession session = sessionRepositoryPort.findPublicById(id)
                .orElseThrow(() -> new SessionNotFoundException(
                        "La sesión con id " + id + " no existe"));

        // Conversión de tipos Long -> Integer con el mismo criterio que el resto de servicios.
        boolean esAutor = session.getUserId() != null && session.getUserId().equals(userId.intValue());

        int views = session.getViews() == null ? 0 : session.getViews();
        if (!esAutor) {
            // Solo cuenta la visualización si el que abre la sesión no es su autor. El incremento es
            // atómico en la base de datos; aquí se refleja en la respuesta sumando uno al valor leído.
            sessionRepositoryPort.incrementViews(id);
            views = views + 1;
        }

        return PublicSessionDTO.builder()
                .id(session.getId())
                .teachingUnitId(session.getTeachingUnitId())
                .userId(session.getUserId())
                .title(session.getTitle())
                .description(session.getDescription())
                .materials(session.getMaterials())
                .totalDuration(session.getTotalDuration())
                .date(session.getDate())
                .warmUpTime(session.getWarmUpTime())
                .warmUpDescription(session.getWarmUpDescription())
                .warmUpObservations(session.getWarmUpObservations())
                .mainPartTime(session.getMainPartTime())
                .mainPartDescription(session.getMainPartDescription())
                .mainPartObservations(session.getMainPartObservations())
                .coolDownTime(session.getCoolDownTime())
                .coolDownDescription(session.getCoolDownDescription())
                .coolDownObservations(session.getCoolDownObservations())
                .active(session.getActive())
                .isPublic(session.getIsPublic())
                .publishedAt(session.getPublishedAt())
                .views(views)
                .downloads(session.getDownloads())
                .author(AuthorDTO.builder()
                        .id(session.getAuthorId())
                        .username(session.getAuthorUsername())
                        .photo(session.getAuthorPhoto())
                        .build())
                .build();
    }
}
