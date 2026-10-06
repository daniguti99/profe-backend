package com.daniel.profe.profe_backend.infraestructure.adapter.in.rest;

import com.daniel.profe.profe_backend.application.dto.session.SessionDTO;
import com.daniel.profe.profe_backend.application.dto.session.SessionListResponseDTO;
import com.daniel.profe.profe_backend.domain.model.User;
import com.daniel.profe.profe_backend.domain.port.in.GetSessionsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Adaptador de entrada REST para obtener las sesiones del usuario autenticado.
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
@RequestMapping("api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final GetSessionsUseCase getSessionsUseCase;

    /**
     * Devuelve las sesiones del usuario autenticado.
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
    @GetMapping
    public ResponseEntity<SessionListResponseDTO> getMisSesiones(Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        List<SessionDTO> sesiones = getSessionsUseCase.getSessions(principal.getId());

        return ResponseEntity.ok(sesiones.isEmpty()
                ? SessionListResponseDTO.sinSesiones()
                : SessionListResponseDTO.conSesiones(sesiones));
    }
}
