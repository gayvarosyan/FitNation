package com.example.fitnationrestapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String BEARER_JWT = "bearer-jwt";

    @Bean
    public OpenAPI fitNationOpenApi() {
        return new OpenAPI()
                .info(new Info().title("FitNation REST API")
                        .version("v1")
                        .description("""
                                HTTP API for FitNation. Most routes require a JWT access token.

                                **Public (no JWT):** `/api/auth/**` (register, login, refresh).

                                **Browser / static (not in this spec):** MVC routes such as `/login`, `/register`, and static `/*.html`, `/css/**`, `/js/**` are served for the web UI.

                                Use **Authorize** and paste only the access token value (Swagger sends `Authorization: Bearer <token>`).
                                """))
                .components(new Components()
                        .addSecuritySchemes(BEARER_JWT,
                                new SecurityScheme()
                                        .name(BEARER_JWT)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT access token from `/api/auth/login` or `/api/auth/register`.")));
    }

    @Bean
    public OpenApiCustomizer bearerJwtExceptAuthPaths() {
        return openApi -> {
            if (openApi.getPaths() == null) {
                return;
            }
            openApi.getPaths().forEach((path, pathItem) -> {
                if (path == null || path.startsWith("/api/auth")) {
                    return;
                }
                pathItem.readOperations().forEach(op ->
                        op.addSecurityItem(new SecurityRequirement().addList(BEARER_JWT)));
            });
        };
    }
}
