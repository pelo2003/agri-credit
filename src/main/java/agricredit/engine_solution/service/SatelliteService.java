package agricredit.engine_solution.service;

import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SatelliteService {

    // In the future, you will inject your OpenFeign client here to call Python
    // @Autowired private PythonAiClient pythonAiClient;

    public Double fetchNdviIndex(Point farmLocation) {
        log.info("Fetching satellite data for Longitude: {}, Latitude: {}",
                farmLocation.getX(), farmLocation.getY());

        // Mocking satellite analysis: Returns a value between -1.0 (dead/water) and +1.0 (healthy)
        // For demonstration, we simulate a moderately healthy farm (0.4 to 0.9)
        return 0.4 + (Math.random() * 0.5);
    }
}
