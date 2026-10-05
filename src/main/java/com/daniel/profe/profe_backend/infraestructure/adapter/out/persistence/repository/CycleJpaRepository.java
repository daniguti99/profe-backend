package com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.repository;

import com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.entity.CycleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CycleJpaRepository extends JpaRepository<CycleEntity, Integer> {
}
