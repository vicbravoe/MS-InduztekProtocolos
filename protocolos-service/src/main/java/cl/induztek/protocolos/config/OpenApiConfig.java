package cl.induztek.protocolos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI().info(new Info()
                .title("API de Protocolos")
                .version("1.0.0")
                .description("Visitas técnicas, protocolos de prueba, pruebas, mediciones, evidencia y estados"));
    }
}
