package com.daniel.profe.profe_backend.domain.port.out;

import com.daniel.profe.profe_backend.application.dto.session.SessionPdfData;

/**
 * Puerto de salida para generar el PDF de una sesión.
 *
 * <p>El dominio y la capa de aplicación no saben cómo se genera el documento: solo piden los bytes
 * a partir de un modelo de datos. La implementación (Thymeleaf + OpenHTMLToPDF) vive en
 * infraestructura.</p>
 */
public interface PdfGeneratorPort {

    /**
     * Genera el PDF de la sesión.
     *
     * @param data datos ya resueltos de la sesión (autor, contadores y contenido)
     * @return contenido binario del PDF
     */
    byte[] generate(SessionPdfData data);
}
