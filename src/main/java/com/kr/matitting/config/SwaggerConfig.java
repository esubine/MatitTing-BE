package com.kr.matitting.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("MatitTing")
                .description("MatitTing API 생성")
                .version("v1.0.0");
    }

    @Bean
    public OpenApiCustomizer customiser() {
        return openApi -> openApi.addServersItem(
                new io.swagger.v3.oas.models.servers.Server()
                        .url("/ws/{any}").description("WebSocket Server"));
    }

}
