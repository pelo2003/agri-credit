package agricredit.engine_solution.dtos;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class FarmerResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String nationalId;
    private String phoneNumber;
    private String primaryCrop;
    private Double farmSizeHectares;
    private String province;
    private String district;
    private String landOwnershipType;
    private Integer farmingExperienceYears;
    private LocalDateTime registeredAt;
    private FarmLocationDTO farmLocation;

    @Data
    @Builder
    public static class FarmLocationDTO {
        private Double latitude;
        private Double longitude;
        private String province;
        private String district;
    }
}