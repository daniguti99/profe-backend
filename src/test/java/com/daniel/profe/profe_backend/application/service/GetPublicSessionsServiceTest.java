package com.daniel.profe.profe_backend.application.service;

import com.daniel.profe.profe_backend.application.dto.session.PublicSessionListResponseDTO;
import com.daniel.profe.profe_backend.application.dto.session.PublicSessionPageDTO;
import com.daniel.profe.profe_backend.application.dto.session.PublicSessionSummaryDTO;
import com.daniel.profe.profe_backend.domain.model.PublicSession;
import com.daniel.profe.profe_backend.domain.model.PublicSessionPage;
import com.daniel.profe.profe_backend.domain.model.PublicSessionSort;
import com.daniel.profe.profe_backend.domain.model.Resource;
import com.daniel.profe.profe_backend.domain.model.Session;
import com.daniel.profe.profe_backend.domain.port.out.SessionRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias del catálogo de sesiones públicas, sin contexto de Spring: el servicio solo
 * depende del puerto de salida, así que basta con un doble en memoria.
 */
class GetPublicSessionsServiceTest {

    /** Doble de test: registra el orden y la paginación recibidos y devuelve lo que se le pida. */
    private static class RepositorioFalso implements SessionRepositoryPort {
        PublicSessionSort ordenRecibido;
        Integer paginaRecibida;
        Integer tamanoRecibido;
        PublicSessionPage aDevolver = PublicSessionPage.builder()
                .sessions(List.of())
                .hasMore(false)
                .build();

        @Override
        public List<Session> findByUserId(Integer userId) {
            return List.of();
        }

        @Override
        public Optional<Session> findById(Integer id) {
            return Optional.empty();
        }

        @Override
        public List<Session> findByTeachingUnitId(Integer teachingUnitId) {
            return List.of();
        }

        @Override
        public Session save(Session session) {
            return null;
        }

        @Override
        public PublicSessionPage findPublicSessions(PublicSessionSort sort, int page, int size) {
            this.ordenRecibido = sort;
            this.paginaRecibida = page;
            this.tamanoRecibido = size;
            return aDevolver;
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
        }

        @Override
        public List<Resource> findResourcesBySessionId(Integer sessionId) {
            return List.of();
        }
    }

    private static PublicSession publica(Integer id, String username) {
        return PublicSession.builder()
                .id(id)
                .title("Sesión " + id)
                .description("Descripción")
                .isPublic(true)
                .active(true)
                .views(0)
                .downloads(0)
                .authorId(7)
                .authorUsername(username)
                .authorPhoto("https://cdn.example.com/foto.png")
                .build();
    }

    private static PublicSessionPage pagina(boolean hasMore, PublicSession... sesiones) {
        return PublicSessionPage.builder()
                .sessions(List.of(sesiones))
                .hasMore(hasMore)
                .build();
    }

    @Test
    void mapeaResumenConElAutorAnidado() {
        RepositorioFalso repositorio = new RepositorioFalso();
        repositorio.aDevolver = pagina(false, publica(1, "daniel"));
        GetPublicSessionsService servicio = new GetPublicSessionsService(repositorio);

        PublicSessionSummaryDTO resumen =
                servicio.getPublicSessions(PublicSessionSort.RECENT, 0, 10).getSessions().get(0);

        assertEquals(1, resumen.getId());
        assertEquals("Sesión 1", resumen.getTitle());
        assertNotNull(resumen.getAuthor());
        assertEquals(7, resumen.getAuthor().getId());
        assertEquals("daniel", resumen.getAuthor().getUsername());
    }

    @Test
    void conservaElOrdenQueDevuelveElRepositorio() {
        RepositorioFalso repositorio = new RepositorioFalso();
        repositorio.aDevolver = pagina(false, publica(3, "a"), publica(1, "b"), publica(2, "c"));
        GetPublicSessionsService servicio = new GetPublicSessionsService(repositorio);

        List<Integer> ids = servicio.getPublicSessions(PublicSessionSort.RECENT, 0, 10)
                .getSessions()
                .stream()
                .map(PublicSessionSummaryDTO::getId)
                .toList();

        assertEquals(List.of(3, 1, 2), ids);
    }

    @Test
    void pasaElOrdenLaPaginaYElTamanoAlRepositorio() {
        RepositorioFalso repositorio = new RepositorioFalso();
        GetPublicSessionsService servicio = new GetPublicSessionsService(repositorio);

        servicio.getPublicSessions(PublicSessionSort.VIEWS, 2, 5);

        assertEquals(PublicSessionSort.VIEWS, repositorio.ordenRecibido);
        assertEquals(2, repositorio.paginaRecibida);
        assertEquals(5, repositorio.tamanoRecibido);
    }

    @Test
    void propagaHasMoreVerdaderoYFalso() {
        RepositorioFalso repositorio = new RepositorioFalso();
        GetPublicSessionsService servicio = new GetPublicSessionsService(repositorio);

        repositorio.aDevolver = pagina(true, publica(1, "a"));
        assertTrue(servicio.getPublicSessions(PublicSessionSort.RECENT, 0, 10).isHasMore());

        repositorio.aDevolver = pagina(false, publica(2, "a"));
        assertFalse(servicio.getPublicSessions(PublicSessionSort.RECENT, 1, 10).isHasMore());
    }

    @Test
    void devuelvePaginaVaciaCuandoNoHaySesionesPublicas() {
        RepositorioFalso repositorio = new RepositorioFalso();
        GetPublicSessionsService servicio = new GetPublicSessionsService(repositorio);

        PublicSessionPageDTO resultado = servicio.getPublicSessions(PublicSessionSort.RECENT, 0, 10);

        assertTrue(resultado.getSessions().isEmpty());
        assertFalse(resultado.isHasMore());
    }

    @Test
    void laEnvolturaDePaginaVaciaLlevaListaVaciaYMensaje() {
        PublicSessionListResponseDTO envoltura = PublicSessionListResponseDTO.dePagina(
                PublicSessionPageDTO.builder().sessions(List.of()).hasMore(false).build(), 0, 10);

        assertTrue(envoltura.getSessions().isEmpty());
        assertEquals(PublicSessionListResponseDTO.MENSAJE_SIN_SESIONES, envoltura.getMessage());
        assertEquals(0, envoltura.getPage());
        assertEquals(10, envoltura.getSize());
        assertFalse(envoltura.isHasMore());
    }

    @Test
    void laEnvolturaConSesionesLlevaMensajeYMetadatosDePagina() {
        PublicSessionListResponseDTO envoltura = PublicSessionListResponseDTO.dePagina(
                PublicSessionPageDTO.builder()
                        .sessions(List.of(PublicSessionSummaryDTO.builder().id(1).build()))
                        .hasMore(true)
                        .build(),
                0, 10);

        assertEquals(PublicSessionListResponseDTO.MENSAJE_CON_SESIONES, envoltura.getMessage());
        assertEquals(1, envoltura.getSessions().size());
        assertTrue(envoltura.isHasMore());
    }

    @Test
    void laEnvolturaFuerzaHasMoreFalsoSiNoHaySesiones() {
        PublicSessionListResponseDTO envoltura = PublicSessionListResponseDTO.dePagina(
                PublicSessionPageDTO.builder().sessions(List.of()).hasMore(true).build(), 999, 10);

        assertFalse(envoltura.isHasMore());
    }
}
