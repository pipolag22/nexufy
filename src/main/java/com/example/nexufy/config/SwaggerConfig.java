package com.example.nexufy.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI(){
        // Configurar el esquema de seguridad JWT
        SecurityScheme securityScheme = new SecurityScheme()
                .name("Authorization") // Nombre del header
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        // Agregar la configuración a OpenAPI
        return new OpenAPI()
                .info(new Info().title("API de Nexufy").version("1.0")
                        .description("Documentación de la API con seguridad JWT"))
                .addSecurityItem(new SecurityRequirement().addList("Authorization"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Authorization", securityScheme));
    }
}
