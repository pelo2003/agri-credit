package agricredit.engine_solution.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MlPredictionResponse {

    @JsonProperty("credit_score")
    private Double creditScore;

    @JsonProperty("risk_tier")
    private String riskTier;

    @JsonProperty("recommended_loan_limit")
    private Double recommendedLoanLimit;

    @JsonProperty("confidence")
    private Double confidence;
}