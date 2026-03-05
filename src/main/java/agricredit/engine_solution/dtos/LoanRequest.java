package agricredit.engine_solution.dtos;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class LoanRequest {
    private Long farmerId;
    private BigDecimal amount;
    private Integer durationMonths;
}
