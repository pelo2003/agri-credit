package agricredit.engine_solution.service;

import agricredit.engine_solution.dtos.MlPredictionRequest;
import agricredit.engine_solution.dtos.MlPredictionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class MlIntegrationService {

    private final WebClient webClient;

    // We no longer ask Spring to inject WebClient.Builder.
    // We only ask for the URL string from the properties file.
    public MlIntegrationService(
            @Value("${ml.api.base-url:http://localhost:8000}") String mlApiBaseUrl) {

        // We use WebClient.create() directly
        this.webClient = WebClient.create(mlApiBaseUrl);
    }

    public MlPredictionResponse getCreditScoreFromModel(MlPredictionRequest request) {
        try {
            return this.webClient.post()
                    .uri("/predict")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(MlPredictionResponse.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to the ML Model service: " + e.getMessage());
        }
    }
}