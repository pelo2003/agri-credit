package agricredit.engine_solution.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MlPredictionRequest {

    @JsonProperty("farmer_profile")
    private FarmerProfileDto farmerProfile;

    @JsonProperty("location_data")
    private LocationDataDto locationData;

    @JsonProperty("agronomic_data")
    private AgronomicDataDto agronomicData;

    @JsonProperty("environmental_risk_factors")
    private EnvironmentalRiskDto environmentalRiskFactors;

    // --- NESTED CLASSES TO BUILD THE JSON STRUCTURE ---

    @Data
    @Builder
    public static class FarmerProfileDto {
        @JsonProperty("farmer_id")
        private String farmerId;

        @JsonProperty("farm_size_hectares")
        private Double farmSizeHectares;

        @JsonProperty("farming_experience_years")
        private Integer farmingExperienceYears;

        @JsonProperty("land_ownership_type")
        private String landOwnershipType;

        @JsonProperty("primary_crop")
        private String primaryCrop;

        @JsonProperty("has_crop_insurance")
        private Boolean hasCropInsurance; // Added!
    }

    @Data
    @Builder
    public static class LocationDataDto {
        private Double latitude;
        private Double longitude;
    }

    @Data
    @Builder
    public static class AgronomicDataDto {
        @JsonProperty("ndvi_score")
        private Double ndviScore;           // Added!

        @JsonProperty("historical_rainfall")
        private Double historicalRainfall;  // Added!

        @JsonProperty("historical_yield")
        private Double historicalYield;     // Added!
    }

    @Data
    @Builder
    public static class EnvironmentalRiskDto {
        @JsonProperty("current_temp_kelvin")
        private Double currentTempKelvin;

        @JsonProperty("humidity_percentage")
        private Integer humidityPercentage;

        @JsonProperty("pressure_hpa")
        private Integer pressureHpa;

        @JsonProperty("wind_speed_mps")
        private Double windSpeedMps;

        @JsonProperty("cloud_cover_percentage")
        private Integer cloudCoverPercentage;
    }
}