package com.daniel.profe.profe_backend.application.dto.session;

/**
 * Resultado de exportar una sesión a PDF: el contenido binario y el nombre de archivo ya generado.
 */
public record SessionPdfExport(String filename, byte[] content) {
}
