package agricredit.engine_solution.dtos;

import lombok.Data;

@Data
public class FarmerRequest {
    private String firstName;
    private String lastName;
    private String nationalId;
    // Make sure these match the Postman JSON exact spelling!
    private String phoneNumber;
    private String primaryCrop;
    private Double farmSizeHectares;

    // Captured via GPS on the Flutter app
    private Double longitude;
    private Double latitude;

    private String password;
}
