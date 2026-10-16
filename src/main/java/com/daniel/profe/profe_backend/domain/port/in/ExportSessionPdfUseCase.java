package com.daniel.profe.profe_backend.domain.port.in;

import com.daniel.profe.profe_backend.application.dto.session.SessionPdfExport;

/**
 * Puerto de entrada para exportar una sesión a PDF.
 *
 * <p>El identificador del usuario no viaja en la URL ni en el cuerpo: llega desde el token y se usa
 * para comprobar los permisos (una sesión privada solo la puede descargar su autor) y para registrar
 * la primera descarga.</p>
 */
public interface ExportSessionPdfUseCase {

    /**
     * Genera el PDF de una sesión aplicando las reglas de acceso y de conteo de descargas.
     *
     * @param sessionId     identificador de la sesión, tomado de la ruta
     * @param currentUserId identificador del usuario tomado del token, nunca de la ruta ni del cuerpo
     * @return el PDF, con su nombre de archivo ya generado
     */
    SessionPdfExport exportPdf(Integer sessionId, Long currentUserId);
}
