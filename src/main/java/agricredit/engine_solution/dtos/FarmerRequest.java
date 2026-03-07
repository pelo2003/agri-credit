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
    private Double longitude;
    private Double latitude;
    private String password;

    // --- NEW REAL-WORLD BANKING FIELDS ---

    // Proof of Land (e.g., "99-Year Lease", "Offer Letter", "Communal")
    private String landOwnershipType;

    // Farming History (Character/Ability in CAMPARI)
    private Integer farmingExperienceYears;
}