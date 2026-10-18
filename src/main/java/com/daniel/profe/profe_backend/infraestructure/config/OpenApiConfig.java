package com.daniel.profe.profe_backend.infraestructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Contrato OpenAPI de la aplicación (el "swagger").
 *
 * <p>springdoc genera el documento a partir de los controladores y DTOs del código; esta clase solo
 * aporta la información general (título, descripción, versión), el esquema de seguridad JWT —para que
 * aparezca el botón <b>Authorize</b>— y el orden de los grupos del menú lateral.</p>
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
                                .bearerFormat("JWT")));
        // La seguridad NO se declara aquí de forma global: cada operación la aporta con
        // @SecurityRequirement, para que login y register no aparezcan con candado en la UI.
    }

    /**
     * Fija el orden de los grupos del menú lateral de la Swagger UI.
     *
     * <p>Hace falta porque springdoc recompone la lista de {@code tags} del documento a partir de las
     * anotaciones {@code @Tag} de los controladores: pierde el orden de declaración y, si la
     * descripción difiere, llega a duplicar entradas. Este customizer global se ejecuta al final y
     * deja la lista con el orden y las descripciones que queremos.</p>
     */
    @Bean
    public GlobalOpenApiCustomizer orderedTagsCustomizer() {
        return openApi -> openApi.setTags(List.of(
                tag("Autenticación", "Registro, inicio de sesión y usuario actual."),
                tag("Sesiones", "Gestión de las sesiones del usuario autenticado."),
                tag("Sesiones públicas", "Catálogo público de sesiones de la comunidad."),
                tag("Unidades didácticas", "Gestión de unidades didácticas del docente autenticado."),
                tag("Cursos", "Listado de cursos."),
                tag("Ciclos", "Listado de ciclos."),
                tag("Exportación", "Exportación de sesiones a PDF.")));
    }

    private static Tag tag(String name, String description) {
        return new Tag().name(name).description(description);
    }
}
