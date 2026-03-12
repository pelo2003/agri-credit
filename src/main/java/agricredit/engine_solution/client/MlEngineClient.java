package agricredit.engine_solution.client;

import agricredit.engine_solution.dtos.MlPredictionRequest;
import agricredit.engine_solution.dtos.MlPredictionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// --- THE LIVE RAILWAY URL GOES HERE ---
@FeignClient(name = "mlEngine", url = "https://agri-credit-ai-engine-production.up.railway.app")
public interface MlEngineClient {

    @PostMapping("/api/v1/score")
    MlPredictionResponse getCreditScore(@RequestBody MlPredictionRequest request);
}