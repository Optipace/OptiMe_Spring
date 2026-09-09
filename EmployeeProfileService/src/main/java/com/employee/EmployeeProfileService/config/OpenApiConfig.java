package com.employee.EmployeeProfileService.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
@AllArgsConstructor
public class OpenApiConfig {

    @Value("${gateway.server.urls}")
    private List<String> serverUrls;

    @Value("${gateway.server.descriptions}")
    private List<String> serverDescription;

    @Bean
    public OpenAPI customOpenAPI() {
        List<Server> servers = new ArrayList<>();
        for (int i = 0; i < serverUrls.size(); i++) {
            servers.add(new Server().url(serverUrls.get(i).trim())
                    .description("Server: " + serverDescription.get(i).trim()));
        }

        return new OpenAPI()
                .servers(servers)
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .name("BearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}