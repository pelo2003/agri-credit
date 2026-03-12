package agricredit.engine_solution.dtos;

import lombok.Data;

@Data
public class PasswordResetRequest {
    private String nationalId;
    private String phoneNumber; // Security check!
    private String newPassword;
}