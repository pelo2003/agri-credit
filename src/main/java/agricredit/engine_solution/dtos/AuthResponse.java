package agricredit.engine_solution.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String phoneNumber; // Replaced nationalId to match login flow
}