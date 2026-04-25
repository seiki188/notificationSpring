package com.seiki.notificationTest.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Notification API")
                        .version("1.0.0")
                        .description("API for real-time notifications with SSE")
                        .contact(new Contact()
                                .name("Developer")
                                .email("gabrielseiki2004@gmail.com")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local server")
                ));
    }
}