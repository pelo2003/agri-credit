package agricredit.engine_solution.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SatelliteService {

    private final WebClient webClient;

    @Value("${agromonitoring.api.key:default_key}")
    private String apiKey;

    public SatelliteService() {
        this.webClient = WebClient.create("http://api.agromonitoring.com/agro/1.0");
    }

    public Double fetchNdviIndex(Double longitude, Double latitude) {
        log.info("Fetching satellite data for Longitude: {}, Latitude: {}", longitude, latitude);
        try {
            log.info("Using fallback simulation for NDVI...");
            return 0.4 + (Math.random() * 0.5);
        } catch (Exception e) {
            log.error("Satellite fetch failed: {}", e.getMessage());
            return 0.5;
        }
    }
}
