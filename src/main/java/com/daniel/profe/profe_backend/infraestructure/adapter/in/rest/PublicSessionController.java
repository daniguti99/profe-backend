package com.daniel.profe.profe_backend.infraestructure.adapter.in.rest;

import com.daniel.profe.profe_backend.application.dto.session.PublicSessionDTO;
import com.daniel.profe.profe_backend.application.dto.session.PublicSessionDetailResponseDTO;
import com.daniel.profe.profe_backend.application.dto.session.PublicSessionListResponseDTO;
import com.daniel.profe.profe_backend.application.dto.session.PublicSessionPageDTO;
import com.daniel.profe.profe_backend.domain.exception.ValidationException;
import com.daniel.profe.profe_backend.domain.model.PublicSessionSort;
import com.daniel.profe.profe_backend.domain.model.User;
import com.daniel.profe.profe_backend.domain.port.in.GetPublicSessionDetailUseCase;
import com.daniel.profe.profe_backend.domain.port.in.GetPublicSessionsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
 * <p><b>Paginación.</b> El listado acepta además {@code ?page=0&size=10} (base 0 y tamaño de 1 a
 * 50). Un valor fuera de rango lanza {@link ValidationException} (400); un valor no numérico lo
 * rechaza la conversión de Spring y lo traduce el manejador global. La respuesta incluye
 * {@code page}, {@code size} y {@code hasMore} para que el cliente pueda pedir la página siguiente
 * sin conocer el total.</p>
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
@Tag(name = "Sesiones públicas", description = "Catálogo público de sesiones de la comunidad.")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("api/public-sessions")
@RequiredArgsConstructor
public class PublicSessionController {

    private final GetPublicSessionsUseCase getPublicSessionsUseCase;
    private final GetPublicSessionDetailUseCase getPublicSessionDetailUseCase;

    /**
     * Devuelve una página del catálogo de sesiones públicas y activas.
     *
     * @param sort criterio de ordenación opcional: {@code recent}, {@code oldest}, {@code downloads}
     *             o {@code views}; si falta, se usa {@code recent}
     * @param page página solicitada (base 0); por defecto 0
     * @param size número de sesiones por página (1..50); por defecto 10
     * @return 200 con la envoltura {@code PublicSessionListResponseDTO}: con la página de sesiones,
     *         sus metadatos de paginación y su mensaje
     */
    @Operation(summary = "Lista el catálogo de sesiones públicas", description = "Ordena con ?sort=recent|oldest|downloads|views (por defecto recent) y pagina con ?page=0&size=10.")
    @GetMapping
    public ResponseEntity<PublicSessionListResponseDTO> getPublicSessions(
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PublicSessionSort criterio = parseSort(sort);
        validarPaginacion(page, size);

        PublicSessionPageDTO pagina = getPublicSessionsUseCase.getPublicSessions(criterio, page, size);

        return ResponseEntity.ok(PublicSessionListResponseDTO.dePagina(pagina, page, size));
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
    @Operation(summary = "Detalle de una sesión pública", description = "404 uniforme si no existe, no es pública o está inactiva.")
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

    /**
     * Valida los parámetros de paginación.
     *
     * <p>La página es base 0 y el tamaño admitido va de 1 a 50. Un valor fuera de rango es un error
     * del cliente (400), igual que un {@code sort} no reconocido. Los valores no numéricos ni
     * siquiera llegan aquí: los rechaza la conversión de Spring y los traduce el manejador global
     * de errores.</p>
     */
    private void validarPaginacion(int page, int size) {
        if (page < 0) {
            throw new ValidationException("La página no puede ser negativa");
        }
        if (size < 1 || size > 50) {
            throw new ValidationException("El tamaño de página debe estar entre 1 y 50");
        }
    }
}
