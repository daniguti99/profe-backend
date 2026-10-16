package com.daniel.profe.profe_backend.infraestructure.adapter.out.pdf;

import com.daniel.profe.profe_backend.application.dto.session.SessionPdfData;
import com.daniel.profe.profe_backend.domain.port.out.PdfGeneratorPort;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

/**
 * Adaptador de salida que genera el PDF de una sesión con Thymeleaf (plantilla HTML) y
 * OpenHTMLToPDF (conversión a PDF).
 *
 * <p>La plantilla {@code templates/session-pdf.html} define el formato de impresión: cabecera solo
 * en la primera página, índice con enlaces internos y número de página en el pie. El adaptador monta
 * el motor de plantillas por su cuenta para no arrastrar la autoconfiguración web de Thymeleaf.</p>
 */
@Component
public class OpenHtmlToPdfAdapter implements PdfGeneratorPort {

    private static final String TEMPLATE = "session-pdf";

    private final TemplateEngine templateEngine;

    public OpenHtmlToPdfAdapter() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(true);

        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(resolver);
    }

    @Override
    public byte[] generate(SessionPdfData data) {
        String html = renderHtml(data);

        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, null);
            builder.toStream(output);
            builder.run();
            return output.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo generar el PDF de la sesión", ex);
        }
    }

    private String renderHtml(SessionPdfData data) {
        Context context = new Context(Locale.forLanguageTag("es-ES"));
        context.setVariable("session", data);
        return templateEngine.process(TEMPLATE, context);
    }
}
