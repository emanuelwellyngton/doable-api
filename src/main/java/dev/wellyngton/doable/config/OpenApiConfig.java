package dev.wellyngton.doable.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI doableOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Doable API")
                        .description("API for Doable Task Management Application")
                        .version("1.0.0"));
    }
}
