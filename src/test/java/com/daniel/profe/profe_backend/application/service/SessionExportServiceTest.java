package com.daniel.profe.profe_backend.application.service;

import com.daniel.profe.profe_backend.application.dto.session.SessionPdfData;
import com.daniel.profe.profe_backend.application.dto.session.SessionPdfExport;
import com.daniel.profe.profe_backend.domain.exception.SessionNotFoundException;
import com.daniel.profe.profe_backend.domain.exception.SessionNotOwnedException;
import com.daniel.profe.profe_backend.domain.model.PublicSession;
import com.daniel.profe.profe_backend.domain.model.PublicSessionSort;
import com.daniel.profe.profe_backend.domain.model.Resource;
import com.daniel.profe.profe_backend.domain.model.Session;
import com.daniel.profe.profe_backend.domain.model.User;
import com.daniel.profe.profe_backend.domain.port.out.DownloadRecorderPort;
import com.daniel.profe.profe_backend.domain.port.out.PdfGeneratorPort;
import com.daniel.profe.profe_backend.domain.port.out.SessionRepositoryPort;
import com.daniel.profe.profe_backend.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias de la exportación de una sesión a PDF, sin contexto de Spring: el servicio solo
 * depende de puertos de salida, así que basta con dobles en memoria.
 */
class SessionExportServiceTest {

    private static final Long AUTOR = 7L;
    private static final Long OTRO = 8L;

    /** Doble de sesiones: devuelve la sesión indicada y registra los recursos pedidos. */
    private static class SesionesFalso implements SessionRepositoryPort {
        Session aDevolver;
        List<Resource> recursos = List.of();
        Integer idIncrementado;

        @Override
        public List<Session> findByUserId(Integer userId) {
            return List.of();
        }

        @Override
        public Optional<Session> findById(Integer id) {
            return Optional.ofNullable(aDevolver);
        }

        @Override
        public List<Session> findByTeachingUnitId(Integer teachingUnitId) {
            return List.of();
        }

        @Override
        public Session save(Session session) {
            return session;
        }

        @Override
        public List<PublicSession> findPublicSessions(PublicSessionSort sort) {
            return List.of();
        }

        @Override
        public Optional<PublicSession> findPublicById(Integer id) {
            return Optional.empty();
        }

        @Override
        public void incrementViews(Integer id) {
        }

        @Override
        public void incrementDownloads(Integer id) {
            this.idIncrementado = id;
        }

        @Override
        public List<Resource> findResourcesBySessionId(Integer sessionId) {
            return recursos;
        }
    }

    /** Doble de usuarios: devuelve siempre el mismo autor. */
    private static class UsuariosFalso implements UserRepositoryPort {
        User autor = User.builder().id(AUTOR).username("druiz").email("druiz@profe.com").build();

        @Override
        public User save(User user) {
            return user;
        }

        @Override
        public Optional<User> findById(Long id) {
            return Optional.ofNullable(autor);
        }

        @Override
        public Optional<User> findByEmail(String email) {
            return Optional.empty();
        }

        @Override
        public Optional<User> findByUsername(String username) {
            return Optional.empty();
        }

        @Override
        public boolean existsByEmail(String email) {
            return false;
        }

        @Override
        public boolean existsByUsername(String username) {
            return false;
        }
    }

    /** Doble del registro de descargas: cuenta cuántas veces se le ha pedido registrar. */
    private static class DescargasFalso implements DownloadRecorderPort {
        int registros;
        Long ultimoUsuario;
        Integer ultimaSesion;

        @Override
        public void registerIfFirst(Long userId, Integer sessionId) {
            this.registros++;
            this.ultimoUsuario = userId;
            this.ultimaSesion = sessionId;
        }
    }

    /** Doble del generador de PDF: devuelve bytes fijos y captura el modelo recibido. */
    private static class PdfFalso implements PdfGeneratorPort {
        SessionPdfData recibido;
        byte[] pdf = {1, 2, 3};

        @Override
        public byte[] generate(SessionPdfData data) {
            this.recibido = data;
            return pdf;
        }
    }

    private static Session sesionPublica() {
        return Session.builder()
                .id(42)
                .userId(AUTOR.intValue())
                .title("Circuito de agilidad")
                .description("Estación 1 y 2")
                .materials("Conos, aros")
                .totalDuration("50 min")
                .date(LocalDate.of(2026, 3, 12))
                .warmUpTime("10 min")
                .warmUpDescription("Trote con cambios de ritmo")
                .mainPartTime("30 min")
                .mainPartDescription("Circuito en parejas")
                .coolDownTime("10 min")
                .coolDownDescription("Vuelta a la calma y estiramientos")
                .active(true)
                .isPublic(true)
                .views(5)
                .downloads(2)
                .build();
    }

    private static SessionExportService servicio(SesionesFalso sesiones, UsuariosFalso usuarios,
            DescargasFalso descargas, PdfFalso pdf) {
        return new SessionExportService(sesiones, usuarios, descargas, pdf);
    }

    @Test
    void exportaUnaSesionPublicaParaCualquierUsuarioAutenticado() {
        SesionesFalso sesiones = new SesionesFalso();
        sesiones.aDevolver = sesionPublica();
        PdfFalso pdf = new PdfFalso();

        SessionPdfExport export = servicio(sesiones, new UsuariosFalso(), new DescargasFalso(), pdf)
                .exportPdf(42, OTRO);

        assertArrayEquals(pdf.pdf, export.content());
        assertNotNull(export.filename());
    }

    @Test
    void permiteAlAutorExportarSuPropiaSesionPrivada() {
        SesionesFalso sesiones = new SesionesFalso();
        sesiones.aDevolver = sesionPublica();
        sesiones.aDevolver.setIsPublic(false);
        PdfFalso pdf = new PdfFalso();

        SessionPdfExport export = servicio(sesiones, new UsuariosFalso(), new DescargasFalso(), pdf)
                .exportPdf(42, AUTOR);

        assertNotNull(export.filename());
    }

    @Test
    void deniegaLaSesionPrivadaAQuienNoEsSuAutor() {
        SesionesFalso sesiones = new SesionesFalso();
        sesiones.aDevolver = sesionPublica();
        sesiones.aDevolver.setIsPublic(false);

        assertThrows(SessionNotOwnedException.class,
                () -> servicio(sesiones, new UsuariosFalso(), new DescargasFalso(), new PdfFalso())
                        .exportPdf(42, OTRO));
    }

    @Test
    void devuelve404CuandoLaSesionNoExiste() {
        SesionesFalso sesiones = new SesionesFalso();
        sesiones.aDevolver = null;

        assertThrows(SessionNotFoundException.class,
                () -> servicio(sesiones, new UsuariosFalso(), new DescargasFalso(), new PdfFalso())
                        .exportPdf(99, AUTOR));
    }

    @Test
    void devuelve404CuandoLaSesionEstaInactiva() {
        SesionesFalso sesiones = new SesionesFalso();
        sesiones.aDevolver = sesionPublica();
        sesiones.aDevolver.setActive(false);

        assertThrows(SessionNotFoundException.class,
                () -> servicio(sesiones, new UsuariosFalso(), new DescargasFalso(), new PdfFalso())
                        .exportPdf(42, AUTOR));
    }

    @Test
    void registraLaDescargaConElUsuarioYLaSesionCorrectos() {
        SesionesFalso sesiones = new SesionesFalso();
        sesiones.aDevolver = sesionPublica();
        DescargasFalso descargas = new DescargasFalso();

        servicio(sesiones, new UsuariosFalso(), descargas, new PdfFalso()).exportPdf(42, OTRO);

        assertEquals(1, descargas.registros);
        assertEquals(OTRO, descargas.ultimoUsuario);
        assertEquals(42, descargas.ultimaSesion);
    }

    @Test
    void incluyeElAutorYLosRecursosEnElModeloDelPdf() {
        SesionesFalso sesiones = new SesionesFalso();
        sesiones.aDevolver = sesionPublica();
        sesiones.recursos = List.of(Resource.builder()
                .id(1)
                .sessionId(42)
                .type("enlace")
                .url("https://cdn.example.com/x")
                .description("Ficha de la sesión")
                .build());
        PdfFalso pdf = new PdfFalso();

        servicio(sesiones, new UsuariosFalso(), new DescargasFalso(), pdf).exportPdf(42, AUTOR);

        assertEquals("druiz", pdf.recibido.getAuthorUsername());
        assertEquals("druiz@profe.com", pdf.recibido.getAuthorEmail());
        assertEquals(1, pdf.recibido.getResources().size());
        assertEquals("Ficha de la sesión", pdf.recibido.getResources().get(0).getDescription());
    }

    @Test
    void omiteLaSeccionDeMaterialesSiSoloHayFecha() {
        SesionesFalso sesiones = new SesionesFalso();
        Session sesion = sesionPublica();
        sesion.setMaterials(null);
        sesion.setTotalDuration(null);
        sesiones.aDevolver = sesion;
        PdfFalso pdf = new PdfFalso();

        servicio(sesiones, new UsuariosFalso(), new DescargasFalso(), pdf).exportPdf(42, AUTOR);

        assertTrue(pdf.recibido.getSections().stream()
                        .noneMatch(s -> s.getTitle().equals("Materiales y duración")),
                "La sección no debería aparecer si no hay materiales ni duración");
    }

    @Test
    void generaElNombreDeArchivoConElTituloLimpiadoYLaFecha() {
        SesionesFalso sesiones = new SesionesFalso();
        sesiones.aDevolver = sesionPublica();
        sesiones.aDevolver.setTitle("Sesión de Fuerza: ¡Piernas!");

        SessionPdfExport export = servicio(sesiones, new UsuariosFalso(), new DescargasFalso(), new PdfFalso())
                .exportPdf(42, AUTOR);

        assertTrue(export.filename().startsWith("Sesion-Sesion-de-Fuerza-Piernas-"),
                "Nombre inesperado: " + export.filename());
        assertTrue(export.filename().endsWith(".pdf"));
        assertTrue(export.filename().matches("Sesion-[A-Za-z0-9-]+-\\d{8}\\.pdf"),
                "Nombre inesperado: " + export.filename());
    }

    @Test
    void usaSinTituloCuandoElTituloEstaVacio() {
        SesionesFalso sesiones = new SesionesFalso();
        sesiones.aDevolver = sesionPublica();
        sesiones.aDevolver.setTitle("   ");

        SessionPdfExport export = servicio(sesiones, new UsuariosFalso(), new DescargasFalso(), new PdfFalso())
                .exportPdf(42, AUTOR);

        assertTrue(export.filename().startsWith("Sesion-Sin-Titulo-"),
                "Nombre inesperado: " + export.filename());
    }
}
