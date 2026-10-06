package com.daniel.profe.profe_backend.infraestructure.adapter.in.rest;

import com.daniel.profe.profe_backend.application.dto.teachingunit.TeachingUnitDTO;
import com.daniel.profe.profe_backend.application.dto.teachingunit.TeachingUnitListResponseDTO;
import com.daniel.profe.profe_backend.domain.model.User;
import com.daniel.profe.profe_backend.domain.port.in.GetTeachingUnitsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Adaptador de entrada REST para obtener las unidades didácticas del docente autenticado.
 *
 * <p><b>Identidad (RF-2 y RF-8).</b> El identificador del usuario sale <b>únicamente</b> del
 * contexto de seguridad: el filtro de JWT deja como principal del {@code Authentication} un
 * {@code domain.model.User} construido con los claims del token, y este controlador se limita a
 * leer su {@code id}. No se lee ningún identificador de la ruta, de la query string ni del cuerpo:
 * el método no declara ninguna anotación de enlace de parámetros (de ruta, de query ni de cuerpo),
 * de modo que no existe por construcción ningún parámetro de entrada que un cliente pueda usar para
 * consultar las unidades de otro usuario. Si la petición llega con parámetros sobrantes
 * ({@code ?userId=<ajeno>}), Spring los descarta y la respuesta es siempre la del token.
 *
 * <p><b>Respuesta (RF-3, RF-5, RF-6).</b> El controlador decide entre las dos factorías del DTO de
 * envoltura, {@code conUnidades(...)} y {@code sinUnidades()}, para que la respuesta sea
 * <b>siempre</b> un objeto {@code { "teachingUnits": [...], "message": "..." }} con código 200:
 * nunca una lista plana, nunca una lista nula y nunca un 404. Los textos del mensaje viven
 * únicamente en las constantes del DTO de envoltura, no aquí.
 *
 * <p><b>Seguridad (RF-7).</b> No se toca la configuración de seguridad: la petición sin token
 * válido ni siquiera llega a este controlador, porque {@code anyRequest().authenticated()} la
 * rechaza antes (respuesta 403).
 *
 * <p>Este es el único punto de la historia donde aparece Spring Security: conforme a la decisión
 * D1 del plan, el adaptador de entrada extrae la identidad y se la pasa al caso de uso como dato,
 * de modo que ni el dominio ni la capa de aplicación dependen del framework de seguridad.
 */
@RestController
@RequestMapping("api/teaching-units")
@RequiredArgsConstructor
public class TeachingUnitController {

    private final GetTeachingUnitsUseCase getTeachingUnitsUseCase;

    /**
     * Devuelve las unidades didácticas del docente autenticado.
     *
     * <p>El caso de uso recibe el {@code id} del principal del {@code Authentication}; el orden de
     * la lista lo garantiza la consulta y el servicio no lo altera, por lo que aquí no se reordena
     * ni se filtra nada.
     *
     * @param authentication contexto de seguridad dejado por el filtro de JWT, cuyo principal es un
     *                       {@code domain.model.User}
     * @return 200 con la envoltura {@code TeachingUnitListResponseDTO}: con la lista y el mensaje
     *         de unidades encontradas, o con lista vacía y el mensaje de unidades no encontradas
     */
    @GetMapping
    public ResponseEntity<TeachingUnitListResponseDTO> getMisTeachingUnits(Authentication authentication) {
        User principal = (User) authentication.getPrincipal();
        List<TeachingUnitDTO> unidades = getTeachingUnitsUseCase.getTeachingUnits(principal.getId());

        return ResponseEntity.ok(unidades.isEmpty()
                ? TeachingUnitListResponseDTO.sinUnidades()
                : TeachingUnitListResponseDTO.conUnidades(unidades));
    }
}