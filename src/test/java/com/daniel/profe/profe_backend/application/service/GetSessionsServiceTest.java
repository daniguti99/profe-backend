package com.daniel.profe.profe_backend.application.service;

import com.daniel.profe.profe_backend.application.dto.session.SessionDTO;
import com.daniel.profe.profe_backend.domain.model.Session;
import com.daniel.profe.profe_backend.domain.port.out.SessionRepositoryPort;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias del caso de uso {@code GET /api/sessions}, sin contexto de Spring
 * (arquitectura hexagonal: el servicio solo depende del puerto de salida, así que basta
 * con un doble en memoria).
 *
 * <p>Sin frameworks de test nuevos: JUnit 5 y las aserciones de JUnit, que ya vienen con
 * los starters de test del proyecto (Constitución, punto 4).</p>
 */
class GetSessionsServiceTest {

    /** Doble de test: registra el identificador recibido y devuelve lo que se le pida. */
    private static class RepositorioFalso implements SessionRepositoryPort {
        Integer usuarioRecibido;
        List<Session> aDevolver = List.of();

        @Override
        public List<Session> findByUserId(Integer userId) {
            this.usuarioRecibido = userId;
            return aDevolver;
        }
    }

    private static Session sesionCompleta() {
        return Session.builder()
                .id(3)
                .teachingUnitId(10)
                .userId(7)
                .title("Circuito de agilidad")
                .description("Estación 1 y 2")
                .materials("Conos, aros")
                .totalDuration("50 min")
                .date(LocalDate.of(2026, 3, 12))
                .warmUpTime("10 min")
                .warmUpDescription("Trote con cambios de ritmo")
                .warmUpGraphicUrl("https://cdn.example.com/warm.png")
                .warmUpObservations("Grupo dispuesto")
                .mainPartTime("30 min")
                .mainPartDescription("Circuito en parejas")
                .mainPartGraphicUrl("https://cdn.example.com/main.png")
                .mainPartObservations("Dos vueltas")
                .coolDownTime("10 min")
                .coolDownDescription("Vuelta a la calma y estiramientos")
                .coolDownGraphicUrl("https://cdn.example.com/cool.png")
                .coolDownObservations("Sin incidencias")
                .build();
    }

    @Test
    void convierteElUserIdLongAIntegerParaElRepositorio() {
        RepositorioFalso repositorio = new RepositorioFalso();
        GetSessionsService servicio = new GetSessionsService(repositorio);

        servicio.getSessions(7L);

        assertEquals(7, repositorio.usuarioRecibido);
    }

    @Test
    void descartaLasSesionesSinRelacionConLaUnidadOElUsuario() {
        RepositorioFalso repositorio = new RepositorioFalso();
        Session sinUnidad = Session.builder().id(1).userId(7).title("Huérfana").build();
        Session sinUsuario = Session.builder().id(2).teachingUnitId(10).title("Sin usuario").build();
        Session valida = sesionCompleta();
        repositorio.aDevolver = List.of(sinUnidad, sinUsuario, valida);
        GetSessionsService servicio = new GetSessionsService(repositorio);

        List<SessionDTO> resultado = servicio.getSessions(7L);

        assertEquals(1, resultado.size());
        assertEquals(valida.getId(), resultado.get(0).getId());
    }

    @Test
    void mapeaLos20CamposDeLaSesionAlDto() {
        RepositorioFalso repositorio = new RepositorioFalso();
        Session original = sesionCompleta();
        repositorio.aDevolver = List.of(original);
        GetSessionsService servicio = new GetSessionsService(repositorio);

        SessionDTO dto = servicio.getSessions(7L).get(0);

        assertEquals(original.getId(), dto.getId());
        assertEquals(original.getTeachingUnitId(), dto.getTeachingUnitId());
        assertEquals(original.getUserId(), dto.getUserId());
        assertEquals(original.getTitle(), dto.getTitle());
        assertEquals(original.getDescription(), dto.getDescription());
        assertEquals(original.getMaterials(), dto.getMaterials());
        assertEquals(original.getTotalDuration(), dto.getTotalDuration());
        assertEquals(original.getDate(), dto.getDate());
        assertEquals(original.getWarmUpTime(), dto.getWarmUpTime());
        assertEquals(original.getWarmUpDescription(), dto.getWarmUpDescription());
        assertEquals(original.getWarmUpGraphicUrl(), dto.getWarmUpGraphicUrl());
        assertEquals(original.getWarmUpObservations(), dto.getWarmUpObservations());
        assertEquals(original.getMainPartTime(), dto.getMainPartTime());
        assertEquals(original.getMainPartDescription(), dto.getMainPartDescription());
        assertEquals(original.getMainPartGraphicUrl(), dto.getMainPartGraphicUrl());
        assertEquals(original.getMainPartObservations(), dto.getMainPartObservations());
        assertEquals(original.getCoolDownTime(), dto.getCoolDownTime());
        assertEquals(original.getCoolDownDescription(), dto.getCoolDownDescription());
        assertEquals(original.getCoolDownGraphicUrl(), dto.getCoolDownGraphicUrl());
        assertEquals(original.getCoolDownObservations(), dto.getCoolDownObservations());
    }

    @Test
    void conservaElOrdenQueDevuelveElRepositorio() {
        RepositorioFalso repositorio = new RepositorioFalso();
        repositorio.aDevolver = IntStream.rangeClosed(1, 3)
                .mapToObj(i -> Session.builder().id(i).teachingUnitId(1).userId(7).build())
                .toList();
        GetSessionsService servicio = new GetSessionsService(repositorio);

        List<SessionDTO> resultado = servicio.getSessions(7L);

        assertEquals(List.of(1, 2, 3), resultado.stream().map(SessionDTO::getId).toList());
    }

    @Test
    void devuelveListaVaciaCuandoElUsuarioNoTieneSesiones() {
        RepositorioFalso repositorio = new RepositorioFalso();
        GetSessionsService servicio = new GetSessionsService(repositorio);

        List<SessionDTO> resultado = servicio.getSessions(7L);

        assertTrue(resultado.isEmpty());
    }
}
