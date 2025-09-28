package com.matchbook.matchbook.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MatchBook API")
                        .version("1.0.0")
                        .description("Sistema de Recomendação de Livros - API REST para cadastro de usuários, livros e sistema de recomendação personalizada")
                        .contact(new Contact()
                                .name("MatchBook Team")
                                .email("contato@matchbook.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
