package com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.adapter;

import com.daniel.profe.profe_backend.domain.model.Session;
import com.daniel.profe.profe_backend.domain.port.out.SessionRepositoryPort;
import com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.entity.SessionEntity;
import com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.repository.SessionJpaRepository;
import com.daniel.profe.profe_backend.infraestructure.mapper.SessionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de salida que traduce las sesiones de la persistencia al dominio.
 *
 * <p>Su única misión es traducir: pide las entidades a {@link SessionJpaRepository} y las
 * convierte en modelos de dominio con {@link SessionMapper}. No decide nada.</p>
 *
 * <p>El filtrado por usuario no se hace aquí, lo garantiza la consulta: la de
 * {@code SessionJpaRepository} lleva ya {@code WHERE s.user.id = :userId AND s.activo = true}, así
 * que solo llegan las sesiones activas del usuario indicado y nunca las de otro. Si esta capa
 * volviera a comprobar el propietario, la garantía quedaría partida entre el SQL y el Java.</p>
 *
 * <p>El orden tampoco se toca: llega desde el {@code ORDER BY s.id ASC} de esa misma consulta y se
 * conserva intacto al traducir, de modo que las cadenas de {@code stream()} posteriores no puedan
 * desordenar el resultado.</p>
 */
@Component
@RequiredArgsConstructor
public class SessionPersistenceAdapter implements SessionRepositoryPort {

    private final SessionJpaRepository repository;
    private final SessionMapper sessionMapper;

    @Override
    public List<Session> findByUserId(Integer userId) {
        return repository.findByUserIdWithRelations(userId).stream()
                .map(sessionMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Session> findById(Integer id) {
        return repository.findById(id)
                .map(sessionMapper::toDomain);
    }

    @Override
    public List<Session> findByTeachingUnitId(Integer teachingUnitId) {
        return repository.findByTeachingUnitId(teachingUnitId).stream()
                .map(sessionMapper::toDomain)
                .toList();
    }

    @Override
    public Session save(Session session) {
        SessionEntity entity = sessionMapper.toEntity(session);
        SessionEntity saved = repository.save(entity);
        return sessionMapper.toDomain(saved);
    }
}
