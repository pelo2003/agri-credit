package agricredit.engine_solution.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {
    @NotBlank(message = "National ID is required")
    private String nationalId;

    @NotBlank(message = "Password is required")
    private String password;
}
