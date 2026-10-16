package com.daniel.profe.profe_backend.domain.port.out;

/**
 * Puerto de salida para registrar las descargas de sesiones.
 *
 * <p>Solo la primera descarga de cada usuario y sesión debe contar para el contador público. Este
 * puerto encapsula esa comprobación para que el caso de uso no conozca la tabla ni el mecanismo de
 * persistencia.</p>
 */
public interface DownloadHistoryPort {

    /**
     * Indica si el usuario ya había descargado antes esta sesión.
     *
     * @param userId    identificador del usuario que descarga
     * @param sessionId identificador de la sesión descargada
     * @return {@code true} si ya existía una descarga previa
     */
    boolean hasDownloaded(Long userId, Integer sessionId);

    /**
     * Registra una nueva descarga del usuario sobre la sesión.
     *
     * @param userId    identificador del usuario que descarga
     * @param sessionId identificador de la sesión descargada
     */
    void recordDownload(Long userId, Integer sessionId);
}
