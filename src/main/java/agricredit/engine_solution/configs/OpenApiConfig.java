package agricredit.engine_solution.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AgriCredit Engine API")
                        .version("1.0")
                        .description("API for Credit Scoring Farmers using Satellite Data")
                        .contact(new Contact()
                                .name("Engineering Team")
                                .email("support@agricredit.com")));
    }
}
