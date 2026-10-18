package com.daniel.profe.profe_backend.infraestructure.adapter.in.rest;

import com.daniel.profe.profe_backend.application.dto.session.CreateSessionRequest;
import com.daniel.profe.profe_backend.application.dto.session.CreateSessionResponse;
import com.daniel.profe.profe_backend.application.dto.session.DeleteSessionResponse;
import com.daniel.profe.profe_backend.application.dto.session.SessionDTO;
import com.daniel.profe.profe_backend.application.dto.session.SessionListResponseDTO;
import com.daniel.profe.profe_backend.application.dto.session.UpdateSessionRequest;
import com.daniel.profe.profe_backend.application.dto.session.UpdateSessionResponse;
import com.daniel.profe.profe_backend.application.dto.session.UpdateVisibilityRequest;
import com.daniel.profe.profe_backend.application.dto.session.UpdateVisibilityResponse;
import com.daniel.profe.profe_backend.domain.model.User;
import com.daniel.profe.profe_backend.domain.port.in.CreateSessionUseCase;
import com.daniel.profe.profe_backend.domain.port.in.DeleteSessionUseCase;
import com.daniel.profe.profe_backend.domain.port.in.GetSessionsUseCase;
import com.daniel.profe.profe_backend.domain.port.in.UpdateSessionUseCase;
import com.daniel.profe.profe_backend.domain.port.in.UpdateSessionVisibilityUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Adaptador de entrada REST para gestionar las sesiones del usuario autenticado.
 *
 * <p><b>Identidad (RF-2 y RF-8).</b> El identificador del usuario sale <b>únicamente</b> del contexto de
 * seguridad: el filtro de JWT deja como principal del {@code Authentication} un
 * {@code domain.model.User} construido con los claims del token, y este controlador se limita a leer
 * su {@code id}. No se lee ningún identificador de la ruta, de la query string ni del cuerpo: el
 * método no declara ninguna anotación de enlace de parámetros (de ruta, de query ni de cuerpo), de
 * modo que no existe por construcción ningún parámetro de entrada que un cliente pueda usar para
 * consultar las sesiones de otro usuario. Si la petición llega con parámetros sobrantes
 * ({@code ?userId=<ajeno>}), Spring los descarta y la respuesta es siempre la del token.
 *
 * <p><b>Respuesta (RF-1, RF-3, RF-5, RF-6).</b> El controlador decide entre las dos factorías del DTO de envoltura,
 * {@code conSesiones(...)} y {@code sinSesiones()}, para que la respuesta sea <b>siempre</b> un
 * objeto {@code { "sessions": [...], "message": "..." }} con código 200: nunca una lista plana,
 * nunca una lista nula y nunca un 404. Los textos del mensaje viven únicamente en las constantes
 * del DTO de envoltura, no aquí.
 *
 * <p><b>Seguridad (RF-7).</b> No se toca la configuración de seguridad: la petición sin token válido ni
 * siquiera llega a este controlador, porque {@code anyRequest().authenticated()} la rechaza antes
 * (respuesta 403).
 *
 * <p>Conforme al patrón del adaptador de entrada, la identidad se extrae aquí y se pasa al caso de
 * uso como dato, de modo que ni el dominio ni la capa de aplicación dependen del framework de
 * seguridad.
 */
@RestController
@Tag(name = "Sesiones", description = "Gestión de las sesiones del usuario autenticado.")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final GetSessionsUseCase getSessionsUseCase;
    private final CreateSessionUseCase createSessionUseCase;
    private final UpdateSessionUseCase updateSessionUseCase;
    private final DeleteSessionUseCase deleteSessionUseCase;
    private final UpdateSessionVisibilityUseCase updateSessionVisibilityUseCase;

    /**
     * Devuelve las sesiones activas del usuario autenticado.
     *
     * <p>El caso de uso recibe el {@code id} del principal del {@code Authentication}; el orden de
     * la lista lo garantiza la consulta y el servicio no lo altera, por lo que aquí no se reordena
     * ni se filtra nada.
     *
     * @param authentication contexto de seguridad dejado por el filtro de JWT, cuyo principal es un
     *                       {@code domain.model.User}
     * @return 200 con la envoltura {@code SessionListResponseDTO}: con la lista y el mensaje de
     *         sesiones encontradas, o con lista vacía y el mensaje de sesiones no encontradas
     */
    @Operation(summary = "Lista las sesiones del usuario autenticado")
    @GetMapping
    public ResponseEntity<SessionListResponseDTO> getMisSesiones(Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        List<SessionDTO> sesiones = getSessionsUseCase.getSessions(principal.getId());

        return ResponseEntity.ok(sesiones.isEmpty()
                ? SessionListResponseDTO.sinSesiones()
                : SessionListResponseDTO.conSesiones(sesiones));
    }

    /**
     * Crea una sesión para el usuario autenticado.
     *
     * <p>El caso de uso recibe el {@code id} del principal del {@code Authentication} y el cuerpo de
     * la petición con los datos de la sesión. El {@code userId} se toma siempre del token, nunca del
     * cuerpo.
     *
     * @param request cuerpo con los datos de la sesión a crear
     * @param authentication contexto de seguridad dejado por el filtro de JWT
     * @return 201 con la sesión creada
     */
    @Operation(summary = "Crea una sesión")
    @PostMapping
    public ResponseEntity<CreateSessionResponse> createSession(@Valid @RequestBody CreateSessionRequest request, Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        CreateSessionResponse response = createSessionUseCase.createSession(request, principal.getId());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Actualiza parcialmente una sesión existente del usuario autenticado.
     *
     * <p>El caso de uso recibe el {@code id} del principal del {@code Authentication}, el
     * identificador de la sesión de la ruta y el cuerpo con los datos a actualizar. Solo se
     * modifican los campos presentes en el cuerpo; los ausentes se dejan intactos. El
     * {@code userId} se toma siempre del token, nunca del cuerpo.
     *
     * @param id identificador de la sesión a actualizar
     * @param request cuerpo con los campos a actualizar de la sesión
     * @param authentication contexto de seguridad dejado por el filtro de JWT
     * @return 200 con la sesión actualizada
     */
    @Operation(summary = "Actualiza parcialmente una sesión")
    @PatchMapping("/{id}")
    public ResponseEntity<UpdateSessionResponse> updateSession(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateSessionRequest request,
            Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        UpdateSessionResponse response = updateSessionUseCase.updateSession(id, request, principal.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Elimina (desactiva) una sesión del usuario autenticado.
     *
     * <p>El caso de uso recibe el {@code id} del principal del {@code Authentication} y el
     * identificador de la sesión de la ruta. El {@code userId} se toma siempre del token.
     *
     * <p>La eliminación es un soft delete: la sesión pasa a {@code active=false} y desaparece de
     * los listados, pero permanece en la base de datos.
     *
     * @param id identificador de la sesión a eliminar
     * @param authentication contexto de seguridad dejado por el filtro de JWT
     * @return 200 con la sesión eliminada
     */
    @Operation(summary = "Elimina (soft delete) una sesión")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteSessionResponse> deleteSession(
            @PathVariable Integer id,
            Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        DeleteSessionResponse response = deleteSessionUseCase.deleteSession(id, principal.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Cambia la visibilidad de una sesión del usuario autenticado (publicar o volver a privada).
     *
     * <p>Es un endpoint dedicado, separado del PATCH de edición, porque la visibilidad es una acción
     * propia con su confirmación: el cuerpo solo lleva el nuevo estado {@code isPublic}. El
     * {@code userId} sale siempre del token, nunca del cuerpo ni de la ruta; así una sesión ajena
     * responde 403 y una inexistente o inactiva, 404.</p>
     *
     * @param id identificador de la sesión de la ruta
     * @param request cuerpo con el nuevo estado de visibilidad
     * @param authentication contexto de seguridad dejado por el filtro de JWT
     * @return 200 con la sesión actualizada y su mensaje
     */
    @Operation(summary = "Publica o vuelve privada una sesión")
    @PatchMapping("/{id}/visibility")
    public ResponseEntity<UpdateVisibilityResponse> updateVisibility(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateVisibilityRequest request,
            Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        UpdateVisibilityResponse response = updateSessionVisibilityUseCase.updateVisibility(id, request, principal.getId());
        return ResponseEntity.ok(response);
    }
}
