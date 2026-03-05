package agricredit.engine_solution.dtos;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class CreditScoreResponse {
    private Double ndviIndex;      // Crop health from satellite
    private Double riskScore;     // AI calculated risk
    private BigDecimal recommendedAmount;
    private BigDecimal interestRate;
    private String assessmentDate;
}
