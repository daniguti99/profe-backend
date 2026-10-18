package com.daniel.profe.profe_backend.application.dto.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Modelo de datos que alimenta la plantilla del PDF de una sesión.
 *
 * <p>Contiene el contenido didáctico de la sesión (los mismos campos del detalle) más el autor, y
 * las dos listas que resuelve el caso de uso: las secciones con contenido para el índice y los
 * recursos por su descripción. Deja fuera los datos administrativos del panel (estado de
 * publicación, fechas y contadores) y las URLs de los recursos: el PDF es un documento de trabajo,
 * no carga imágenes ni enlaces y solo escribe texto.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionPdfData {

    private String title;
    private String description;

    private String materials;
    private String totalDuration;

    private String warmUpTime;
    private String warmUpDescription;
    private String warmUpObservations;

    private String mainPartTime;
    private String mainPartDescription;
    private String mainPartObservations;

    private String coolDownTime;
    private String coolDownDescription;
    private String coolDownObservations;

    private String authorUsername;
    private String authorEmail;

    /** Índice de secciones que realmente tienen contenido, en el orden del documento. */
    private List<PdfSection> sections;

    /** Recursos asociados, mostrados solo por su descripción. */
    private List<PdfResource> resources;

    /** Entrada del índice del PDF: ancla y título de la sección. */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PdfSection {
        private String anchor;
        private String title;
    }

    /** Recurso asociado mostrado en el PDF (sin URL). */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PdfResource {
        private String type;
        private String description;
    }
}
