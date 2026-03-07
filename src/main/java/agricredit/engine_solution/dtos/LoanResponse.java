package agricredit.engine_solution.dtos;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class LoanResponse {
    private Long loanId;
    private String farmerName;
    private BigDecimal principal;
    private BigDecimal interestRate;
    private Double creditScore;
    private String status;

    private String loanPurpose;
    private String expectedOffTaker;

    // --- NEW FIELD FOR TRANSPARENCY ---
    private String rejectionReason;
}