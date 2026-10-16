package com.daniel.profe.profe_backend.infraestructure.adapter.out.persistence.adapter;

import com.daniel.profe.profe_backend.domain.port.out.DownloadHistoryPort;
import com.daniel.profe.profe_backend.domain.port.out.DownloadRecorderPort;
import com.daniel.profe.profe_backend.domain.port.out.SessionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Adaptador de salida que registra la primera descarga de un usuario sobre una sesión.
 *
 * <p>Se ejecuta en su propia transacción ({@code REQUIRES_NEW}) para que el registro de la descarga y
 * el incremento del contador no arrastren a la transacción de la exportación: si dos peticiones
 * simultáneas intentan registrar la misma descarga, la restricción {@code UNIQUE} de la tabla hace
 * que solo una cuente y la otra se descarta sin romper la generación del PDF.</p>
 */
@Component
@RequiredArgsConstructor
public class DownloadRecorderAdapter implements DownloadRecorderPort {

    private final DownloadHistoryPort downloadHistoryPort;
    private final SessionRepositoryPort sessionRepositoryPort;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW,
            noRollbackFor = DataIntegrityViolationException.class)
    public void registerIfFirst(Long userId, Integer sessionId) {
        if (downloadHistoryPort.hasDownloaded(userId, sessionId)) {
            // Ya la había descargado antes: no vuelve a contar.
            return;
        }

        try {
            downloadHistoryPort.recordDownload(userId, sessionId);
            sessionRepositoryPort.incrementDownloads(sessionId);
        } catch (DataIntegrityViolationException ex) {
            // Otra petición simultánea registró la misma descarga antes: no se cuenta dos veces.
        }
    }
}
