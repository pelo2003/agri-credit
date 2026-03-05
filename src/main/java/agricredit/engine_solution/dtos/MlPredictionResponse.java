package agricredit.engine_solution.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MlPredictionResponse {
    private double predictedCreditScore;
    private String riskCategory;
    private double defaultProbability;
}