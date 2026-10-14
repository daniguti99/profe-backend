package com.daniel.profe.profe_backend.application.service;

import com.daniel.profe.profe_backend.application.dto.session.AuthorDTO;
import com.daniel.profe.profe_backend.application.dto.session.PublicSessionSummaryDTO;
import com.daniel.profe.profe_backend.domain.model.PublicSession;
import com.daniel.profe.profe_backend.domain.model.PublicSessionSort;
import com.daniel.profe.profe_backend.domain.port.in.GetPublicSessionsUseCase;
import com.daniel.profe.profe_backend.domain.port.out.SessionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Caso de uso para obtener el catálogo de sesiones públicas.
 *
 * <p>El servicio es una función pura del criterio de ordenación: no lee el contexto de seguridad,
 * no conoce el transporte y no decide el formato de la respuesta HTTP. Solo orquesta el puerto de
 * salida y traduce de dominio a DTO.</p>
 *
 * <p>No reordena ni filtra la lista: el orden lo garantiza la consulta (que además exige
 * {@code publico = true AND activo = true}) y el mapeo secuencial lo conserva.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional
public class GetPublicSessionsService implements GetPublicSessionsUseCase {

    private final SessionRepositoryPort sessionRepositoryPort;

    @Override
    public List<PublicSessionSummaryDTO> getPublicSessions(PublicSessionSort sort) {
        return sessionRepositoryPort.findPublicSessions(sort).stream()
                .map(this::toSummary)
                .toList();
    }

    /**
     * Traduce el modelo de lectura al resumen que viaja al cliente. La información del autor se
     * anida en un {@link AuthorDTO}, siempre presente aunque su foto sea nula.
     */
    private PublicSessionSummaryDTO toSummary(PublicSession session) {
        return PublicSessionSummaryDTO.builder()
                .id(session.getId())
                .title(session.getTitle())
                .description(session.getDescription())
                .date(session.getDate())
                .publishedAt(session.getPublishedAt())
                .views(session.getViews())
                .downloads(session.getDownloads())
                .author(AuthorDTO.builder()
                        .id(session.getAuthorId())
                        .username(session.getAuthorUsername())
                        .photo(session.getAuthorPhoto())
                        .build())
                .build();
    }
}
