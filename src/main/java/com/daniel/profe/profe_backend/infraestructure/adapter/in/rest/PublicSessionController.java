package com.daniel.profe.profe_backend.infraestructure.adapter.in.rest;

import com.daniel.profe.profe_backend.application.dto.session.PublicSessionDTO;
import com.daniel.profe.profe_backend.application.dto.session.PublicSessionDetailResponseDTO;
import com.daniel.profe.profe_backend.application.dto.session.PublicSessionListResponseDTO;
import com.daniel.profe.profe_backend.application.dto.session.PublicSessionSummaryDTO;
import com.daniel.profe.profe_backend.domain.exception.ValidationException;
import com.daniel.profe.profe_backend.domain.model.PublicSessionSort;
import com.daniel.profe.profe_backend.domain.model.User;
import com.daniel.profe.profe_backend.domain.port.in.GetPublicSessionDetailUseCase;
import com.daniel.profe.profe_backend.domain.port.in.GetPublicSessionsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

/**
 * Adaptador de entrada REST para explorar las sesiones públicas de la comunidad.
 *
 * <p><b>Identidad.</b> El listado no depende del usuario. El detalle sí recibe el identificador del
 * usuario, pero siempre desde el contexto de seguridad: el filtro de JWT deja como principal del
 * {@code Authentication} un {@code domain.model.User} y este controlador se limita a leer su
 * {@code id}. Ese dato solo sirve para decidir si la consulta genera una visualización (RF-11); no
 * se expone en la respuesta.</p>
 *
 * <p><b>Orden (RF-6 y RF-7).</b> El listado acepta {@code ?sort=recent|oldest|downloads|views}. La
 * traducción del texto al enum de dominio se hace aquí, en el borde, y un valor no reconocido lanza
 * {@link ValidationException}, que el manejador global traduce a 400. La ausencia del parámetro cae
 * en {@code recent}, el valor por defecto.</p>
 *
 * <p><b>404 unificado (RF-9 y RF-10).</b> El detalle no distingue entre sesión inexistente, privada
 * o inactiva: la consulta las convierte en un único resultado vacío y el servicio lanza
 * {@code SessionNotFoundException}, que responde 404. Así el detalle nunca revela la existencia de
 * sesiones privadas ajenas.</p>
 *
 * <p><b>Seguridad.</b> No se toca la configuración: las rutas pasan por
 * {@code anyRequest().authenticated()}, de modo que una petición sin token válido responde 403 antes
 * de llegar aquí.</p>
 */
@RestController
@RequestMapping("api/public-sessions")
@RequiredArgsConstructor
public class PublicSessionController {

    private final GetPublicSessionsUseCase getPublicSessionsUseCase;
    private final GetPublicSessionDetailUseCase getPublicSessionDetailUseCase;

    /**
     * Devuelve el catálogo de sesiones públicas y activas.
     *
     * @param sort criterio de ordenación opcional: {@code recent}, {@code oldest}, {@code downloads}
     *             o {@code views}; si falta, se usa {@code recent}
     * @return 200 con la envoltura {@code PublicSessionListResponseDTO}: con la lista y su mensaje, o
     *         con lista vacía y el mensaje de sesiones no encontradas
     */
    @GetMapping
    public ResponseEntity<PublicSessionListResponseDTO> getPublicSessions(
            @RequestParam(required = false) String sort) {
        PublicSessionSort criterio = parseSort(sort);
        List<PublicSessionSummaryDTO> sessions = getPublicSessionsUseCase.getPublicSessions(criterio);

        return ResponseEntity.ok(sessions.isEmpty()
                ? PublicSessionListResponseDTO.sinSesiones()
                : PublicSessionListResponseDTO.conSesiones(sessions));
    }

    /**
     * Devuelve el detalle de una sesión pública.
     *
     * <p>El caso de uso recibe el {@code id} del principal del {@code Authentication}; solo se usa
     * para no contar la visualización cuando quien consulta es el propio autor.</p>
     *
     * @param id identificador de la sesión de la ruta
     * @param authentication contexto de seguridad dejado por el filtro de JWT
     * @return 200 con el detalle y su mensaje, o 404 si la sesión no es pública y activa
     */
    @GetMapping("/{id}")
    public ResponseEntity<PublicSessionDetailResponseDTO> getPublicSession(
            @PathVariable Integer id,
            Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        PublicSessionDTO session = getPublicSessionDetailUseCase.getPublicSession(id, principal.getId());

        return ResponseEntity.ok(PublicSessionDetailResponseDTO.deSesion(session));
    }

    /**
     * Traduce el texto de la query string al enum de ordenación.
     *
     * <p>Es tolerante con mayúsculas y espacios para no rechazar variantes razonables del mismo
     * criterio. Cualquier otro valor es un error del cliente (400).</p>
     */
    private PublicSessionSort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return PublicSessionSort.RECENT;
        }
        return switch (sort.trim().toLowerCase(Locale.ROOT)) {
            case "recent" -> PublicSessionSort.RECENT;
            case "oldest" -> PublicSessionSort.OLDEST;
            case "downloads" -> PublicSessionSort.DOWNLOADS;
            case "views" -> PublicSessionSort.VIEWS;
            default -> throw new ValidationException("El criterio de ordenación no es válido");
        };
    }
}
