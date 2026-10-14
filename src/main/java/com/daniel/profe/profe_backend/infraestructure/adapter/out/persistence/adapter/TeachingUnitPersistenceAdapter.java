package com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.adapter;

import com.daniel.profe.profe_backend.domain.model.TeachingUnit;
import com.daniel.profe.profe_backend.domain.port.out.TeachingUnitRepositoryPort;
import com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.entity.TeachingUnitEntity;
import com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.repository.TeachingUnitJpaRepository;
import com.daniel.profe.profe_backend.infraestructure.mapper.TeachingUnitMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de salida que traduce las unidades didácticas de la persistencia al dominio.
 *
 * <p>Su única misión es traducir: pide las entidades a {@link TeachingUnitJpaRepository} y las
 * convierte en modelos de dominio con {@link TeachingUnitMapper}. No decide nada.</p>
 *
 * <p>El filtrado por usuario no se hace aquí, lo garantiza la consulta: la de
 * {@code TeachingUnitJpaRepository} lleva ya {@code WHERE tu.user.id = :userId AND tu.activo = true},
 * así que solo llegan las unidades activas del usuario indicado y nunca las de otro. Si esta capa
 * volviera a comprobar el propietario, la garantía quedaría partida entre el SQL y el Java.</p>
 *
 * <p>El orden tampoco se toca: llega desde el {@code ORDER BY tu.id ASC} de esa misma consulta y se
 * conserva intacto al traducir, de modo que las cadenas de {@code stream()} posteriores no pueden
 * desordenar el resultado.</p>
 *
 * <p>La guarda defensiva que descarta las unidades a las que les falte alguna relación tampoco
 * corresponde a esta capa: el adapter traduce, no descarta, y esa decisión es del servicio.</p>
 */
@Component
@RequiredArgsConstructor
public class TeachingUnitPersistenceAdapter implements TeachingUnitRepositoryPort {

    private final TeachingUnitJpaRepository repository;
    private final TeachingUnitMapper teachingUnitMapper;

    @Override
    public List<TeachingUnit> findByUserId(Integer userId) {
        return repository.findByUserIdWithRelations(userId).stream()
                .map(teachingUnitMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<TeachingUnit> findById(Integer id) {
        return repository.findById(id)
                .map(teachingUnitMapper::toDomain);
    }

    @Override
    public TeachingUnit save(TeachingUnit teachingUnit) {
        TeachingUnitEntity entity = teachingUnitMapper.toEntity(teachingUnit);
        TeachingUnitEntity saved = repository.save(entity);
        return teachingUnitMapper.toDomain(saved);
    }
}
