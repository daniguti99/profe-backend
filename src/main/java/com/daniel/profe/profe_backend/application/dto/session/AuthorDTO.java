package com.daniel.profe.profe_backend.application.dto.session;

import lombok.*;

/**
 * Información básica del autor de una sesión pública.
 *
 * <p>Expone únicamente identificador, nombre de usuario y foto: los datos mínimos para atribuir la
 * sesión, sin correo, provincia ni localidad.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorDTO {

    private Integer id;
    private String username;
    private String photo;
}
