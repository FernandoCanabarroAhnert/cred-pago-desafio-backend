package com.fernandocanabarro.desafio_credpago.openapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
@OpenAPIDefinition
public class OpenAPIConfig {

    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
            .info(new Info()
                .title("Desafio Backend CredPago")
                .version("FernandoCanabarroAhnert")
                .description("Este é um projeto baseado no desafio proposto pela CredPago, em que há as funcionalidades de CRUD de produtos, adicionar e remover itens do Carrinho de Compras e realizar Transações.")
                )
                .externalDocs(new ExternalDocumentation()
                    .description("Link GitHub do Desafio proposto")
                    .url("https://github.com/lucasmpw/desafio-backend"));

    }
}
