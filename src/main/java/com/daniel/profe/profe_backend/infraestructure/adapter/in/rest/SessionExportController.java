package com.daniel.profe.profe_backend.infraestructure.adapter.in.rest;

import com.daniel.profe.profe_backend.application.dto.session.SessionPdfExport;
import com.daniel.profe.profe_backend.domain.model.User;
import com.daniel.profe.profe_backend.domain.port.in.ExportSessionPdfUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

/**
 * Adaptador de entrada REST para exportar una sesión a PDF.
 *
 * <p><b>Identidad.</b> El identificador del usuario sale únicamente del contexto de seguridad: el
 * filtro de JWT deja como principal del {@code Authentication} un {@code domain.model.User} y este
 * controlador se limita a leer su {@code id}. Ese dato se usa para comprobar los permisos (una
 * sesión privada solo la puede descargar su autor) y para registrar la primera descarga.</p>
 *
 * <p><b>Respuesta.</b> Devuelve el PDF como descarga con {@code Content-Disposition: attachment} y
 * el nombre de archivo generado por el caso de uso. El controlador no conoce Thymeleaf ni
 * OpenHTMLToPDF: solo recibe los bytes.</p>
 */
@RestController
@RequestMapping("api/sessions")
@RequiredArgsConstructor
public class SessionExportController {

    private static final MediaType PDF = MediaType.APPLICATION_PDF;

    private final ExportSessionPdfUseCase exportSessionPdfUseCase;

    /**
     * Exporta a PDF una sesión del usuario autenticado o una sesión pública de cualquier autor.
     *
     * @param id             identificador de la sesión de la ruta
     * @param authentication contexto de seguridad dejado por el filtro de JWT
     * @return 200 con el PDF como adjunto
     */
    @GetMapping("/{id}/export/pdf")
    public ResponseEntity<byte[]> exportPdf(
            @PathVariable Integer id,
            Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        SessionPdfExport export = exportSessionPdfUseCase.exportPdf(id, principal.getId());

        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(export.filename(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .contentLength(export.content().length)
                .body(export.content());
    }
}
