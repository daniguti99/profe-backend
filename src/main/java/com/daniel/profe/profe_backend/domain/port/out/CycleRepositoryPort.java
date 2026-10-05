package com.daniel.profe.profe_backend.domain.port.out;

import com.daniel.profe.profe_backend.domain.model.Cycle;

import java.util.List;

public interface CycleRepositoryPort {
    List<Cycle> findAll();
}
