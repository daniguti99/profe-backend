package com.daniel.profe.profe_backend.application.service;

import com.daniel.profe.profe_backend.application.dto.teachingunit.TeachingUnitDTO;
import com.daniel.profe.profe_backend.domain.port.in.GetTeachingUnitsUseCase;
import com.daniel.profe.profe_backend.domain.port.out.TeachingUnitRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Caso de uso para obtener las unidades didácticas del docente indicado.
 *
 * <p>El servicio es una función pura de {@code userId}: no lee el contexto de seguridad
 * ({@code SecurityContextHolder}), no conoce el transporte y no decide el formato de la respuesta
 * HTTP. Solo orquesta el puerto de salida y traduce de dominio a DTO.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class GetTeachingUnitsService implements GetTeachingUnitsUseCase {

    private final TeachingUnitRepositoryPort teachingUnitRepository;

    /**
     * Devuelve las unidades didácticas del usuario ya mapeadas al DTO de respuesta.
     *
     * @param userId identificador del docente tomado del token, nunca de la ruta ni del cuerpo
     * @return lista de unidades; vacía si el docente no tiene ninguna (RF-5)
     */
    @Override
    public List<TeachingUnitDTO> getTeachingUnits(Long userId) {
        // Conversión de tipos Long -> Integer. Se usa intValue() y no Math.toIntExact() a
        // propósito: ante un "sub" absurdo, el truncamiento simplemente no encuentra filas y
        // produce una lista vacía coherente, mientras que toIntExact() lanzaría ArithmeticException
        // y devolvería un 500 sin sentido para lo que en realidad es una petición sin resultados.
        return teachingUnitRepository.findByUserId(userId.intValue())
                .stream()
                // Filtro defensivo: protege el contrato del DTO, según el cual
                // userId y courseId nunca son nulos. La garantía de verdad está en el JOIN FETCH
                // interno de la consulta, que ya descarta esas filas en SQL; esto es la red de
                // seguridad por si otro adapter implementa el puerto o si alguien afloja la consulta.
                // Es imprescindible porque el mapper no avisa de una relación nula: con MapStruct
                // 1.6.3 el código generado la convierte en un userId o courseId a null, en silencio y
                // sin excepción, y ese null llegaría al DTO. Va antes del map por claridad del
                // encadenado; el orden en sí no altera el resultado, porque el mapper ya se ejecutó
                // dentro del adapter.
                .filter(u -> u.getUserId() != null && u.getCourseId() != null)
                // Mapeo secuencial (sin parallel()) para preservar el orden que devuelve el SQL.
                // Se rellenan los 7 campos, incluidos los de texto, que viajan tal cual aunque
                // vengan vacíos o nulos (RF-4).
                .map(u -> TeachingUnitDTO.builder()
                        .id(u.getId())
                        .userId(u.getUserId())
                        .courseId(u.getCourseId())
                        .title(u.getTitle())
                        .description(u.getDescription())
                        .schedule(u.getSchedule())
                        .notes(u.getNotes())
                        .build())
                .toList();
        // Sin try/catch deliberadamente: un fallo interno debe propagarse hasta el manejador de
        // excepciones de Spring, que lo traduce a 500. Capturarlo aquí devolvería una lista vacía
        // con el mensaje de "sin unidades" y ocultaría el error real (RF-9).
    }
}