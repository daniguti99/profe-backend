package com.daniel.profe.profe_backend.application.service;

import com.daniel.profe.profe_backend.application.dto.session.PublicSessionDTO;
import com.daniel.profe.profe_backend.domain.exception.SessionNotFoundException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas unitarias del detalle de una sesión pública, sin contexto de Spring: el servicio solo
 * depende del puerto de salida, así que basta con un doble en memoria.
 */
class GetPublicSessionDetailServiceTest {

    /** Doble de test: devuelve una sesión pública y registra las visualizaciones incrementadas. */
    private static class RepositorioFalso implements SessionRepositoryPort {
        PublicSession aDevolver;
        int vistasIncrementadas;

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
            return List.of();
        }

        @Override
        public Optional<PublicSession> findPublicById(Integer id) {
            return Optional.ofNullable(aDevolver);
        }

        @Override
        public void incrementViews(Integer id) {
            this.vistasIncrementadas++;
        }

        @Override
        public void incrementDownloads(Integer id) {
        }

        @Override
        public List<Resource> findResourcesBySessionId(Integer sessionId) {
            return List.of();
        }
    }

    private static PublicSession publicaDelAutor(int userId) {
        return PublicSession.builder()
                .id(1)
                .teachingUnitId(10)
                .userId(userId)
                .title("Circuito")
                .isPublic(true)
                .active(true)
                .views(5)
                .downloads(2)
                .authorId(userId)
                .authorUsername("daniel")
                .authorPhoto("https://cdn.example.com/foto.png")
                .build();
    }

    @Test
    void mapeaElDetalleConElAutorAnidado() {
        RepositorioFalso repositorio = new RepositorioFalso();
        repositorio.aDevolver = publicaDelAutor(7);
        GetPublicSessionDetailService servicio = new GetPublicSessionDetailService(repositorio);

        PublicSessionDTO detalle = servicio.getPublicSession(1, 99L);

        assertEquals(1, detalle.getId());
        assertEquals("Circuito", detalle.getTitle());
        assertNotNull(detalle.getAuthor());
        assertEquals(7, detalle.getAuthor().getId());
        assertEquals("daniel", detalle.getAuthor().getUsername());
    }

    @Test
    void detalleInexistenteOPrivadoLanzaNotFound() {
        RepositorioFalso repositorio = new RepositorioFalso();
        GetPublicSessionDetailService servicio = new GetPublicSessionDetailService(repositorio);

        assertThrows(SessionNotFoundException.class, () -> servicio.getPublicSession(1, 7L));
    }

    @Test
    void consultadoPorOtroUsuarioIncrementaLaVisualizacion() {
        RepositorioFalso repositorio = new RepositorioFalso();
        repositorio.aDevolver = publicaDelAutor(7);
        GetPublicSessionDetailService servicio = new GetPublicSessionDetailService(repositorio);

        PublicSessionDTO detalle = servicio.getPublicSession(1, 99L);

        assertEquals(1, repositorio.vistasIncrementadas);
        assertEquals(6, detalle.getViews());
    }

    @Test
    void consultadoPorElAutorNoIncrementaLaVisualizacion() {
        RepositorioFalso repositorio = new RepositorioFalso();
        repositorio.aDevolver = publicaDelAutor(7);
        GetPublicSessionDetailService servicio = new GetPublicSessionDetailService(repositorio);

        PublicSessionDTO detalle = servicio.getPublicSession(1, 7L);

        assertEquals(0, repositorio.vistasIncrementadas);
        assertEquals(5, detalle.getViews());
    }
}
