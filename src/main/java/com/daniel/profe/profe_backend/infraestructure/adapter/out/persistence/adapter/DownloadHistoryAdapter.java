package com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.adapter;

import com.daniel.profe.profe_backend.domain.port.out.DownloadHistoryPort;
import com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.entity.SessionDownloadEntity;
import com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.repository.SessionDownloadJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Adaptador de salida que traduce el puerto {@link DownloadHistoryPort} a consultas JPA.
 *
 * <p>Su única misión es traducir: el dominio decide cuándo cuenta una descarga, este adaptador solo
 * comprueba la existencia previa y guarda el registro.</p>
 */
@Component
@RequiredArgsConstructor
public class DownloadHistoryAdapter implements DownloadHistoryPort {

    private final SessionDownloadJpaRepository repository;

    @Override
    public boolean hasDownloaded(Long userId, Integer sessionId) {
        return repository.existsBySessionIdAndUserId(sessionId, userId.intValue());
    }

    @Override
    public void recordDownload(Long userId, Integer sessionId) {
        repository.save(SessionDownloadEntity.builder()
                .sessionId(sessionId)
                .userId(userId.intValue())
                .downloadDate(LocalDateTime.now())
                .build());
    }
}
