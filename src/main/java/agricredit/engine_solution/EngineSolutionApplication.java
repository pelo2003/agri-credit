package agricredit.engine_solution;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients; // Add this

@SpringBootApplication
@EnableFeignClients // Add this! Tells Spring to look for HTTP clients
public class EngineSolutionApplication {
	public static void main(String[] args) {
		SpringApplication.run(EngineSolutionApplication.class, args);
	}
}