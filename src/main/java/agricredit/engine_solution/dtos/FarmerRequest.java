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

    // --- THE FIX: We removed Lat/Lon and added province ---
    private String province;

    private String password;

    // --- NEW REAL-WORLD BANKING FIELDS ---

    // Proof of Land (e.g., "99-Year Lease", "Offer Letter", "Communal")
    private String landOwnershipType;

    // Farming History (Character/Ability in CAMPARI)
    private Integer farmingExperienceYears;
}