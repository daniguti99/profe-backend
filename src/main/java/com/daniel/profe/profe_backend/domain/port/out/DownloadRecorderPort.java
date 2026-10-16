package com.daniel.profe.profe_backend.domain.port.out;

/**
 * Puerto de salida para contar la descarga de una sesión.
 *
 * <p>Solo la primera descarga de cada usuario sobre cada sesión debe incrementar el contador. La
 * comprobación y la escritura se encapsulan aquí, en su propia transacción, para que el caso de uso
 * de exportación no conozca la tabla de descargas ni el mecanismo de concurrencia.</p>
 */
public interface DownloadRecorderPort {

    /**
     * Cuenta la descarga si es la primera del usuario sobre la sesión; si ya existía, no hace nada.
     *
     * @param userId    identificador del usuario que descarga
     * @param sessionId identificador de la sesión descargada
     */
    void registerIfFirst(Long userId, Integer sessionId);
}
