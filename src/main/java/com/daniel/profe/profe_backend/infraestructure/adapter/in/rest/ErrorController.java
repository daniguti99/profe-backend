package com.daniel.profe.profe_backend.infraestructure.adapter.in.rest;

import com.daniel.profe.profe_backend.application.dto.error.ErrorResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Controlador propio de la ruta {@code /error}: cualquier error que el
 * contenedor reenvíe internamente a {@code /error} se responde con el
 * formato {@link ErrorResponse} del resto de la API, en lugar del
 * formato por defecto de Spring Boot.
 */
@RestController
public class ErrorController implements org.springframework.boot.webmvc.error.ErrorController {

    private static final int DEFAULT_STATUS = HttpStatus.INTERNAL_SERVER_ERROR.value();

    private static final Map<Integer, String> MESSAGES = Map.of(
            HttpStatus.BAD_REQUEST.value(), "Solicitud incorrecta",
            HttpStatus.UNAUTHORIZED.value(), "No autorizado",
            HttpStatus.FORBIDDEN.value(), "Prohibido",
            HttpStatus.NOT_FOUND.value(), "Recurso no encontrado",
            HttpStatus.METHOD_NOT_ALLOWED.value(), "Método no permitido",
            HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error interno del servidor"
    );

    @RequestMapping("/error")
    public ResponseEntity<ErrorResponse> error(HttpServletRequest request) {
        int status = resolveStatus(request);
        return ResponseEntity
                .status(status)
                .body(ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(status)
                        .error(messageFor(status))
                        .build());
    }

    private int resolveStatus(HttpServletRequest request) {
        Object attribute = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (attribute instanceof Integer statusCode) {
            return statusCode;
        }
        return DEFAULT_STATUS;
    }

    private String messageFor(int status) {
        String message = MESSAGES.get(status);
        if (message != null) {
            return message;
        }
        HttpStatus resolved = HttpStatus.resolve(status);
        return resolved != null ? resolved.getReasonPhrase() : "Error";
    }
}
