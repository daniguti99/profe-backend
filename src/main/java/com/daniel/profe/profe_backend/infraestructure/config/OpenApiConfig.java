package com.daniel.profe.profe_backend.infraestructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Contrato OpenAPI de la aplicación (el "swagger").
 *
 * <p>springdoc genera el documento a partir de los controladores y DTOs del código; esta clase solo
 * aporta la información general (título, descripción, versión) y el esquema de seguridad JWT, de modo
 * que en la Swagger UI aparezca el botón <b>Authorize</b> y se pueda probar cualquier endpoint
 * autenticado con un token real.</p>
 *
 * <p>La UI está en {@code /swagger-ui.html} y el contrato JSON en {@code /v3/api-docs}, ambos
 * permitidos sin token en {@code SecurityConfig}.</p>
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI profeOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("ProFE API")
                        .description("""
                                API del backend de ProFE.

                                Autenticación con JWT: obtén el token en `POST /api/auth/login` y pégalo en \
                                el botón **Authorize** con el prefijo `Bearer ` para probar los endpoints \
                                autenticados.

                                Los recursos se eliminan con soft delete: los elementos borrados dejan de \
                                aparecer en los listados pero permanecen en base de datos.
                                """)
                        .version("1.0.0"))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME, new SecurityScheme()
                                .name(SECURITY_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                // Todas las operaciones requieren token por defecto; los endpoints públicos se marcan
                // con @SecurityRequirements en sus controladores.
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME));
    }
}
