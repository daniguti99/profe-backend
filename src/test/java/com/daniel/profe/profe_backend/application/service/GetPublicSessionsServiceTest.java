package com.daniel.profe.profe_backend.application.service;

import com.daniel.profe.profe_backend.application.dto.session.PublicSessionListResponseDTO;
import com.daniel.profe.profe_backend.application.dto.session.PublicSessionSummaryDTO;
import com.daniel.profe.profe_backend.domain.model.PublicSession;
import com.daniel.profe.profe_backend.domain.model.PublicSessionSort;
import com.daniel.profe.profe_backend.domain.model.Resource;
import com.daniel.profe.profe_backend.domain.model.Session;
import com.daniel.profe.profe_backend.domain.port.out.SessionRepositoryPort;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias del catálogo de sesiones públicas, sin contexto de Spring: el servicio solo
 * depende del puerto de salida, así que basta con un doble en memoria.
 */
class GetPublicSessionsServiceTest {

    /** Doble de test: registra el orden recibido y devuelve lo que se le pida. */
    private static class RepositorioFalso implements SessionRepositoryPort {
        PublicSessionSort ordenRecibido;
        List<PublicSession> aDevolver = List.of();

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
        public List<PublicSession> findPublicSessions(PublicSessionSort sort) {
            this.ordenRecibido = sort;
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

    @Test
    void mapeaResumenConElAutorAnidado() {
        RepositorioFalso repositorio = new RepositorioFalso();
        repositorio.aDevolver = List.of(publica(1, "daniel"));
        GetPublicSessionsService servicio = new GetPublicSessionsService(repositorio);

        PublicSessionSummaryDTO resumen = servicio.getPublicSessions(PublicSessionSort.RECENT).get(0);

        assertEquals(1, resumen.getId());
        assertEquals("Sesión 1", resumen.getTitle());
        assertNotNull(resumen.getAuthor());
        assertEquals(7, resumen.getAuthor().getId());
        assertEquals("daniel", resumen.getAuthor().getUsername());
    }

    @Test
    void conservaElOrdenQueDevuelveElRepositorio() {
        RepositorioFalso repositorio = new RepositorioFalso();
        repositorio.aDevolver = List.of(publica(3, "a"), publica(1, "b"), publica(2, "c"));
        GetPublicSessionsService servicio = new GetPublicSessionsService(repositorio);

        List<Integer> ids = servicio.getPublicSessions(PublicSessionSort.RECENT)
                .stream()
                .map(PublicSessionSummaryDTO::getId)
                .toList();

        assertEquals(List.of(3, 1, 2), ids);
    }

    @Test
    void pasaElOrdenAlRepositorio() {
        RepositorioFalso repositorio = new RepositorioFalso();
        GetPublicSessionsService servicio = new GetPublicSessionsService(repositorio);

        servicio.getPublicSessions(PublicSessionSort.VIEWS);

        assertEquals(PublicSessionSort.VIEWS, repositorio.ordenRecibido);
    }

    @Test
    void devuelveListaVaciaCuandoNoHaySesionesPublicas() {
        RepositorioFalso repositorio = new RepositorioFalso();
        GetPublicSessionsService servicio = new GetPublicSessionsService(repositorio);

        assertTrue(servicio.getPublicSessions(PublicSessionSort.RECENT).isEmpty());
    }

    @Test
    void laEnvolturaSinSesionesLlevaListaVaciaYMensaje() {
        PublicSessionListResponseDTO envoltura = PublicSessionListResponseDTO.sinSesiones();

        assertTrue(envoltura.getSessions().isEmpty());
        assertEquals(PublicSessionListResponseDTO.MENSAJE_SIN_SESIONES, envoltura.getMessage());
    }
}
