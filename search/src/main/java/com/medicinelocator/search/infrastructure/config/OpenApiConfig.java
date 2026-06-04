package com.medicinelocator.search.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        SecurityScheme bearerScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        return new OpenAPI()
                .info(new Info()
                        .title("Medicine Locator — Search & Location Service")
                        .description("""
                                Read-only medicine discovery service.
                                Searches pharmacy inventories by medicine name (with fuzzy matching),
                                filters by proximity using PostGIS, and returns pharmacy location data
                                for map rendering on the frontend.
                                No commands. No inventory mutations. Pure CQRS query side.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Medicine Locator Platform")
                                .email("support@medicinelocator.com")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", bearerScheme));
    }
}