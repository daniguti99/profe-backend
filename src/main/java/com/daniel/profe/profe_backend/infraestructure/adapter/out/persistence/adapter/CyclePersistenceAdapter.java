package com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.adapter;

import com.daniel.profe.profe_backend.domain.model.Cycle;
import com.daniel.profe.profe_backend.domain.port.out.CycleRepositoryPort;
import com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.repository.CycleJpaRepository;
import com.daniel.profe.profe_backend.infraestructure.mapper.CycleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CyclePersistenceAdapter implements CycleRepositoryPort {

    private final CycleJpaRepository repository;
    private final CycleMapper cycleMapper;

    @Override
    public List<Cycle> findAll() {
        return repository.findAll().stream()
                .map(cycleMapper::toDomain)
                .toList();
    }
}
