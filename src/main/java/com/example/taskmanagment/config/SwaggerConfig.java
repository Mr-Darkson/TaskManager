package com.example.taskmanagment.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(
                        List.of(
                                new Server()
                                        .url("http://localhost:8080/")
                        )
                )
                .info(new Info()
                        .title("Менеджер задач")
                        .version("1.0")
                        .description("""
                                Сервис управления задачами (Task Management System) 
                                предназначен для простого и эффективного управления задачами. 
                                Он предоставляет RESTful API для выполнения операций создания,
                                редактирования, удаления и просмотра задач.
                               
                                """)
                )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));

    }

}
