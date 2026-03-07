package agricredit.engine_solution.dtos;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class LoanRequest {
    private Long farmerId;
    private BigDecimal amount;
    private Integer durationMonths;

    // --- NEW REAL-WORLD BANKING FIELDS ---

    // Clear Loan Purpose (e.g., "Production - Maize Seed", "Capex - Tractor")
    private String loanPurpose;

    // Stop Order Agreement (e.g., "Grain Marketing Board", "Tobacco Auction Floors")
    private String expectedOffTaker;

    // Risk Mitigation (Mandatory for real agricultural loans)
    private Boolean hasCropInsurance;
}