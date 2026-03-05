package agricredit.engine_solution.configs;

import org.n52.jackson.datatype.jts.JtsModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public JtsModule jtsModule() {
        // This module allows Jackson to serialize and deserialize
        // JTS Geometry types like Point, Polygon, etc.
        return new JtsModule();
    }
}
