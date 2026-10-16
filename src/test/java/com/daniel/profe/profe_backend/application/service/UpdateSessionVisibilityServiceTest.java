package com.daniel.profe.profe_backend.application.service;

import com.daniel.profe.profe_backend.application.dto.session.UpdateVisibilityRequest;
import com.daniel.profe.profe_backend.application.dto.session.UpdateVisibilityResponse;
import com.daniel.profe.profe_backend.domain.exception.SessionNotOwnedException;
import com.daniel.profe.profe_backend.domain.exception.SessionNotFoundException;
import com.daniel.profe.profe_backend.domain.model.PublicSession;
import com.daniel.profe.profe_backend.domain.model.PublicSessionSort;
import com.daniel.profe.profe_backend.domain.model.Resource;
import com.daniel.profe.profe_backend.domain.model.Session;
import com.daniel.profe.profe_backend.domain.port.out.SessionRepositoryPort;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias del cambio de visibilidad de una sesión, sin contexto de Spring: el servicio
 * solo depende del puerto de salida, así que basta con un doble en memoria.
 */
class UpdateSessionVisibilityServiceTest {

    /** Doble de test: devuelve una sesión y registra la que se guarda. */
    private static class RepositorioFalso implements SessionRepositoryPort {
        Session aDevolver;
        Session guardada;

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
            this.guardada = session;
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
        }

        @Override
        public List<Resource> findResourcesBySessionId(Integer sessionId) {
            return List.of();
        }
    }

    private static Session sesionPropia(boolean activa) {
        return Session.builder()
                .id(1)
                .teachingUnitId(10)
                .userId(7)
                .title("Circuito")
                .active(activa)
                .isPublic(false)
                .build();
    }

    @Test
    void publicarFijaLaFechaDePublicacion() {
        RepositorioFalso repositorio = new RepositorioFalso();
        repositorio.aDevolver = sesionPropia(true);
        UpdateSessionVisibilityService servicio = new UpdateSessionVisibilityService(repositorio);

        UpdateVisibilityResponse respuesta = servicio.updateVisibility(
                1, new UpdateVisibilityRequest(true), 7L);

        assertTrue(respuesta.getSession().getIsPublic());
        assertNotNull(respuesta.getSession().getPublishedAt());
        assertNotNull(repositorio.guardada.getPublishedAt());
        assertTrue(repositorio.guardada.getIsPublic());
    }

    @Test
    void despublicarConservaLaFechaDePublicacion() {
        LocalDateTime publicadaEl = LocalDateTime.of(2026, 3, 12, 9, 30);
        RepositorioFalso repositorio = new RepositorioFalso();
        Session sesion = sesionPropia(true);
        sesion.setIsPublic(true);
        sesion.setPublishedAt(publicadaEl);
        repositorio.aDevolver = sesion;
        UpdateSessionVisibilityService servicio = new UpdateSessionVisibilityService(repositorio);

        UpdateVisibilityResponse respuesta = servicio.updateVisibility(
                1, new UpdateVisibilityRequest(false), 7L);

        assertFalse(respuesta.getSession().getIsPublic());
        assertEquals(publicadaEl, repositorio.guardada.getPublishedAt());
        assertEquals(publicadaEl, respuesta.getSession().getPublishedAt());
    }

    @Test
    void republicarActualizaLaFechaDePublicacion() {
        LocalDateTime antigua = LocalDateTime.now().minusDays(10);
        RepositorioFalso repositorio = new RepositorioFalso();
        Session sesion = sesionPropia(true);
        sesion.setIsPublic(true);
        sesion.setPublishedAt(antigua);
        repositorio.aDevolver = sesion;
        UpdateSessionVisibilityService servicio = new UpdateSessionVisibilityService(repositorio);

        servicio.updateVisibility(1, new UpdateVisibilityRequest(true), 7L);

        assertTrue(repositorio.guardada.getPublishedAt().isAfter(antigua));
    }

    @Test
    void sesionInexistenteLanzaNotFound() {
        RepositorioFalso repositorio = new RepositorioFalso();
        UpdateSessionVisibilityService servicio = new UpdateSessionVisibilityService(repositorio);

        assertThrows(SessionNotFoundException.class,
                () -> servicio.updateVisibility(99, new UpdateVisibilityRequest(true), 7L));
    }

    @Test
    void sesionAjenaLanzaNotOwned() {
        RepositorioFalso repositorio = new RepositorioFalso();
        Session ajena = sesionPropia(true);
        ajena.setUserId(99);
        repositorio.aDevolver = ajena;
        UpdateSessionVisibilityService servicio = new UpdateSessionVisibilityService(repositorio);

        assertThrows(SessionNotOwnedException.class,
                () -> servicio.updateVisibility(1, new UpdateVisibilityRequest(true), 7L));
    }

    @Test
    void sesionInactivaLanzaNotFound() {
        RepositorioFalso repositorio = new RepositorioFalso();
        repositorio.aDevolver = sesionPropia(false);
        UpdateSessionVisibilityService servicio = new UpdateSessionVisibilityService(repositorio);

        assertThrows(SessionNotFoundException.class,
                () -> servicio.updateVisibility(1, new UpdateVisibilityRequest(true), 7L));
        assertNull(repositorio.guardada);
    }

    @Test
    void devuelveLaMismaSesionQueGuarda() {
        RepositorioFalso repositorio = new RepositorioFalso();
        repositorio.aDevolver = sesionPropia(true);
        UpdateSessionVisibilityService servicio = new UpdateSessionVisibilityService(repositorio);

        servicio.updateVisibility(1, new UpdateVisibilityRequest(true), 7L);

        assertSame(repositorio.guardada.getId(), repositorio.aDevolver.getId());
    }
}
