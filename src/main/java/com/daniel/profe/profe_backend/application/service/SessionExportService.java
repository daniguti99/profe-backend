package com.daniel.profe.profe_backend.application.service;

import com.daniel.profe.profe_backend.application.dto.session.SessionPdfData;
import com.daniel.profe.profe_backend.application.dto.session.SessionPdfExport;
import com.daniel.profe.profe_backend.domain.exception.SessionNotFoundException;
import com.daniel.profe.profe_backend.domain.exception.SessionNotOwnedException;
import com.daniel.profe.profe_backend.domain.model.Resource;
import com.daniel.profe.profe_backend.domain.model.Session;
import com.daniel.profe.profe_backend.domain.model.User;
import com.daniel.profe.profe_backend.domain.port.in.ExportSessionPdfUseCase;
import com.daniel.profe.profe_backend.domain.port.out.DownloadRecorderPort;
import com.daniel.profe.profe_backend.domain.port.out.PdfGeneratorPort;
import com.daniel.profe.profe_backend.domain.port.out.SessionRepositoryPort;
import com.daniel.profe.profe_backend.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Caso de uso para exportar una sesión a PDF.
 *
 * <p><b>Permisos.</b> Una sesión pública la puede descargar cualquier usuario autenticado; una
 * privada, solo su autor. La comprobación sigue el orden de la edición y el borrado: existencia
 * (404), propiedad (403) y actividad (404).</p>
 *
 * <p><b>Conteo.</b> Solo cuenta la primera descarga de cada usuario sobre cada sesión; las
 * siguientes generan igualmente el PDF pero no incrementan el contador. La escritura se delega en
 * {@link DownloadRecorderPort}, que la aísla en su propia transacción.</p>
 *
 * <p>Es una función pura de {@code id} y {@code userId}: no lee el contexto de seguridad, no conoce
 * el transporte ni decide el formato de la respuesta HTTP.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SessionExportService implements ExportSessionPdfUseCase {

    private static final String DEFAULT_TITLE = "Sin-Titulo";
    private static final DateTimeFormatter FILE_DATE = DateTimeFormatter.BASIC_ISO_DATE;

    private final SessionRepositoryPort sessionRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final DownloadRecorderPort downloadRecorderPort;
    private final PdfGeneratorPort pdfGeneratorPort;

    @Override
    public SessionPdfExport exportPdf(Integer sessionId, Long currentUserId) {
        Session session = sessionRepositoryPort.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException(
                        "La sesión con id " + sessionId + " no existe"));

        boolean esAutor = session.getUserId() != null
                && session.getUserId().equals(currentUserId.intValue());

        // Una sesión privada solo la puede descargar su autor; una pública, cualquier usuario.
        if (!Boolean.TRUE.equals(session.getIsPublic()) && !esAutor) {
            throw new SessionNotOwnedException("La sesión no te pertenece");
        }

        if (!Boolean.TRUE.equals(session.getActive())) {
            throw new SessionNotFoundException(
                    "La sesión con id " + sessionId + " no existe");
        }

        // Solo cuenta la primera descarga del usuario; las siguientes no alteran el contador.
        downloadRecorderPort.registerIfFirst(currentUserId, sessionId);

        SessionPdfData data = buildData(session);
        byte[] pdf = pdfGeneratorPort.generate(data);

        return new SessionPdfExport(buildFilename(session.getTitle()), pdf);
    }

    private SessionPdfData buildData(Session session) {
        List<Resource> recursos = sessionRepositoryPort.findResourcesBySessionId(session.getId());

        Optional<User> author = session.getUserId() == null
                ? Optional.empty()
                : userRepositoryPort.findById(session.getUserId().longValue());

        List<SessionPdfData.PdfResource> pdfResources = recursos.stream()
                .map(r -> SessionPdfData.PdfResource.builder()
                        .type(r.getType())
                        .description(r.getDescription())
                        .build())
                .toList();

        return SessionPdfData.builder()
                .title(session.getTitle())
                .description(session.getDescription())
                .materials(session.getMaterials())
                .totalDuration(session.getTotalDuration())
                .warmUpTime(session.getWarmUpTime())
                .warmUpDescription(session.getWarmUpDescription())
                .warmUpObservations(session.getWarmUpObservations())
                .mainPartTime(session.getMainPartTime())
                .mainPartDescription(session.getMainPartDescription())
                .mainPartObservations(session.getMainPartObservations())
                .coolDownTime(session.getCoolDownTime())
                .coolDownDescription(session.getCoolDownDescription())
                .coolDownObservations(session.getCoolDownObservations())
                .authorUsername(author.map(User::getUsername).orElse(null))
                .authorEmail(author.map(User::getEmail).orElse(null))
                .resources(pdfResources)
                .sections(buildSections(session, !pdfResources.isEmpty()))
                .build();
    }

    /**
     * Construye el índice del PDF con las secciones que realmente tienen contenido.
     */
    private List<SessionPdfData.PdfSection> buildSections(Session session, boolean hasResources) {
        List<SessionPdfData.PdfSection> sections = new ArrayList<>();

        if (hasText(session.getDescription())) {
            sections.add(section("descripcion", "Descripción"));
        }
        if (hasText(session.getMaterials()) || hasText(session.getTotalDuration())) {
            sections.add(section("materiales", "Materiales y duración"));
        }
        if (hasText(session.getWarmUpTime()) || hasText(session.getWarmUpDescription())
                || hasText(session.getWarmUpObservations())) {
            sections.add(section("calentamiento", "Calentamiento"));
        }
        if (hasText(session.getMainPartTime()) || hasText(session.getMainPartDescription())
                || hasText(session.getMainPartObservations())) {
            sections.add(section("parte-principal", "Parte principal"));
        }
        if (hasText(session.getCoolDownTime()) || hasText(session.getCoolDownDescription())
                || hasText(session.getCoolDownObservations())) {
            sections.add(section("vuelta-a-la-calma", "Vuelta a la calma"));
        }
        if (hasResources) {
            sections.add(section("recursos", "Recursos"));
        }

        return sections;
    }

    private SessionPdfData.PdfSection section(String anchor, String title) {
        return SessionPdfData.PdfSection.builder()
                .anchor(anchor)
                .title(title)
                .build();
    }

    /**
     * Genera el nombre del archivo: {@code Sesion-<titulo>-<YYYYMMDD>.pdf}.
     *
     * <p>El título se limpia de acentos y de caracteres no alfanuméricos (que pasan a guiones) para
     * que el nombre sea válido en cualquier sistema de archivos. Si el título queda vacío se usa
     * {@code Sin-Titulo}.</p>
     */
    private String buildFilename(String title) {
        String base = title == null ? "" : title.trim();
        String limpio = Normalizer.normalize(base, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .replaceAll("[^A-Za-z0-9]+", "-")
                .replaceAll("(^-+)|(-+$)", "");

        if (limpio.isBlank()) {
            limpio = DEFAULT_TITLE;
        }

        return "Sesion-" + limpio + "-" + LocalDate.now().format(FILE_DATE) + ".pdf";
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
