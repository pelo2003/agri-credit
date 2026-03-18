package agricredit.engine_solution.dtos;

import lombok.Data;

@Data
public class FarmerRequest {
    private String firstName;
    private String lastName;
    private String nationalId;
    private String phoneNumber;
    private String primaryCrop;
    private Double farmSizeHectares;

    private String province;
    private String district;

    private String pin; // farmer's PIN used as login password

    private String landOwnershipType;
    private Integer farmingExperienceYears;
}