package com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.repository;

import com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.entity.SessionDownloadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionDownloadJpaRepository extends JpaRepository<SessionDownloadEntity, Long> {

    boolean existsBySessionIdAndUserId(Integer sessionId, Integer userId);
}
