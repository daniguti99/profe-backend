package com.daniel.profe.profe_backend.application.service;

import com.daniel.profe.profe_backend.application.dto.session.SessionDTO;
import com.daniel.profe.profe_backend.domain.port.in.GetSessionsUseCase;
import com.daniel.profe.profe_backend.domain.port.out.SessionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Caso de uso para obtener las sesiones del usuario autenticado.
 *
 * <p>El servicio es una función pura de {@code userId}: no lee el contexto de seguridad
 * ({@code SecurityContextHolder}), no conoce el transporte y no decide el formato de la respuesta
 * HTTP. Solo orquesta el puerto de salida y traduce de dominio a DTO.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class GetSessionsService implements GetSessionsUseCase {

    private final SessionRepositoryPort sessionRepository;

    /**
     * Devuelve las sesiones del usuario ya mapeadas al DTO de respuesta.
     *
     * @param userId identificador del usuario tomado del token, nunca de la ruta ni del cuerpo
     * @return lista de sesiones; vacía si el usuario no tiene ninguna
     */
    @Override
    public List<SessionDTO> getSessions(Long userId) {
        // Conversión de tipos Long -> Integer. Se usa intValue() y no Math.toIntExact() a
        // propósito: ante un "sub" absurdo, el truncamiento simplemente no encuentra filas y
        // produce una lista vacía coherente, mientras que toIntExact() lanzaría ArithmeticException
        // y devolvería un 500 sin sentido para lo que en realidad es una petición sin resultados.
        // El porqué de los dos tipos está documentado en GetSessionsUseCase.
        return sessionRepository.findByUserId(userId.intValue())
                .stream()
                // Filtro defensivo: protege el contrato del DTO, según el cual
                // teachingUnitId y userId nunca son nulos. La garantía de verdad está en el
                // JOIN FETCH interno de la consulta, que ya descarta esas filas en SQL; esto es la
                // red de seguridad por si otro adapter implementa el puerto o si alguien afloja la
                // consulta. Es imprescindible porque el mapper no avisa de una relación nula: con
                // MapStruct 1.6.3 el código generado la convierte en un teachingUnitId o userId a
                // null, en silencio y sin excepción, y ese null llegaría al DTO. Va antes del map
                // por claridad del encadenado; el orden en sí no altera el resultado, porque el
                // mapper ya se ejecutó dentro del adapter.
                .filter(s -> s.getTeachingUnitId() != null && s.getUserId() != null)
                // Mapeo secuencial (sin parallel()) para preservar el orden que devuelve el SQL.
                // Se rellenan los 20 campos, incluidos los de texto y los de fecha, que viajan tal
                // cual aunque vengan vacíos o nulos.
                .map(s -> SessionDTO.builder()
                        .id(s.getId())
                        .teachingUnitId(s.getTeachingUnitId())
                        .userId(s.getUserId())
                        .title(s.getTitle())
                        .description(s.getDescription())
                        .materials(s.getMaterials())
                        .totalDuration(s.getTotalDuration())
                        .date(s.getDate())
                        .warmUpTime(s.getWarmUpTime())
                        .warmUpDescription(s.getWarmUpDescription())
                        .warmUpGraphicUrl(s.getWarmUpGraphicUrl())
                        .warmUpObservations(s.getWarmUpObservations())
                        .mainPartTime(s.getMainPartTime())
                        .mainPartDescription(s.getMainPartDescription())
                        .mainPartGraphicUrl(s.getMainPartGraphicUrl())
                        .mainPartObservations(s.getMainPartObservations())
                        .coolDownTime(s.getCoolDownTime())
                        .coolDownDescription(s.getCoolDownDescription())
                        .coolDownGraphicUrl(s.getCoolDownGraphicUrl())
                        .coolDownObservations(s.getCoolDownObservations())
                        .build())
                .toList();
        // Sin try/catch deliberadamente: un fallo interno debe propagarse hasta el manejador de
        // excepciones de Spring, que lo traduce a 500. Capturarlo aquí devolvería una lista vacía
        // con el mensaje de "sin sesiones" y ocultaría el error real.
    }
}
