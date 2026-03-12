package agricredit.engine_solution.service;

import agricredit.engine_solution.client.MlEngineClient;
import agricredit.engine_solution.dtos.MlPredictionRequest;
import agricredit.engine_solution.dtos.MlPredictionResponse;
import agricredit.engine_solution.entity.Farmer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MlIntegrationService {

    private final MlEngineClient mlEngineClient; // Inject the Python bridge

    public Map<String, Object> assessLoanRisk(Farmer farmer, Double requestedAmount, Double ndviScore, boolean hasCropInsurance) {
        log.info("Sending Farmer data to Python ML Engine for ID: {}", farmer.getId());

        String province = farmer.getProvince(); // Coming straight from the Flutter app dropdown!

        // --- THE LOCATION PIVOT ---
        // 1. Get exact coordinates for the Python payload
        double[] coordinates = getRegionCoordinates(province);
        double latitude = coordinates[0];
        double longitude = coordinates[1];

        // 2. Get accurate rainfall data based on the Agro-Ecological Region of that Province
        double predictedRainfallMm = getZimbabweRegionalRainfall(province);

        // 3. Predict yield based on crop type and farm size
        double historicalYieldTons = getPredictedYield(farmer.getFarmSizeHectares(), farmer.getPrimaryCrop());

        log.info("Geospatial Engine mapped '{}' to (Lat: {}, Lon: {}). Estimated {}mm rainfall and {} tons yield.",
                province, latitude, longitude, Math.round(predictedRainfallMm), historicalYieldTons);

        // 1. Build the nested JSON payload matching the exact agreed structure for Python
        MlPredictionRequest requestPayload = MlPredictionRequest.builder()
                .farmerProfile(MlPredictionRequest.FarmerProfileDto.builder()
                        .farmerId(farmer.getId().toString())
                        .farmSizeHectares(farmer.getFarmSizeHectares())
                        .farmingExperienceYears(farmer.getFarmingExperienceYears())
                        .landOwnershipType(farmer.getLandOwnershipType())
                        .primaryCrop(farmer.getPrimaryCrop())
                        .hasCropInsurance(hasCropInsurance)
                        .build())
                .locationData(MlPredictionRequest.LocationDataDto.builder()
                        .longitude(longitude)
                        .latitude(latitude)
                        .build())
                .agronomicData(MlPredictionRequest.AgronomicDataDto.builder()
                        .ndviScore(ndviScore)
                        .historicalRainfall(predictedRainfallMm)
                        .historicalYield(historicalYieldTons)
                        .build())
                .environmentalRiskFactors(MlPredictionRequest.EnvironmentalRiskDto.builder()
                        // Hardcoding general weather metrics so the Python team receives the exact payload structure
                        .currentTempKelvin(295.81)
                        .humidityPercentage(77)
                        .pressureHpa(1010)
                        .windSpeedMps(1.21)
                        .cloudCoverPercentage(99)
                        .build())
                .build();

        // 2. MAKE THE HTTP CALL TO PYTHON!
        MlPredictionResponse mlResponse;
        try {
            mlResponse = mlEngineClient.getCreditScore(requestPayload);
            log.info("Received ML Score: {} with Tier: {}", mlResponse.getCreditScore(), mlResponse.getRiskTier());
        } catch (Exception e) {
            log.error("Failed to connect to Python Engine. Is FastAPI running on port 8000? Error: {}", e.getMessage());
            throw new RuntimeException("AI Credit Scoring Engine is currently unavailable.");
        }

        // 3. Process the AI response back into your Spring Boot workflow
        String status = "PENDING";
        String rejectionReason = "Passed AI screening. Awaiting final review.";

        // Use Python's recommended loan limit to reject over-leverage
        if (requestedAmount > mlResponse.getRecommendedLoanLimit()) {
            status = "REJECTED";
            rejectionReason = "Requested amount ($" + requestedAmount + ") exceeds AI recommended capacity ($" + mlResponse.getRecommendedLoanLimit() + ").";
        } else if (mlResponse.getRiskTier().equals("HIGH")) {
            status = "REJECTED";
            rejectionReason = "AI classified profile as HIGH RISK. Score: " + mlResponse.getCreditScore();
        }

        double interestRate = Math.max(8.0, 20.0 - (mlResponse.getCreditScore() / 10));

        Map<String, Object> result = new HashMap<>();
        result.put("creditScore", mlResponse.getCreditScore());
        result.put("status", status);
        result.put("interestRate", interestRate);
        result.put("reason", rejectionReason);

        return result;
    }

    // =========================================================================
    // HACKATHON GEOSPATIAL HELPERS
    // =========================================================================

    private double[] getRegionCoordinates(String province) {
        if (province == null || province.trim().isEmpty()) {
            return new double[]{-19.0154, 29.1549}; // Default center of Zimbabwe
        }

        String formattedProvince = province.trim().toUpperCase().replace(" ", "_");

        return switch (formattedProvince) {
            case "HARARE" -> new double[]{-17.8248, 31.0530};
            case "BULAWAYO" -> new double[]{-20.1500, 28.5833};
            case "MANICALAND" -> new double[]{-18.9728, 32.6694};
            case "MASHONALAND_WEST" -> new double[]{-17.4851, 29.7889};
            case "MASHONALAND_EAST" -> new double[]{-18.7333, 31.8333};
            case "MASHONALAND_CENTRAL" -> new double[]{-17.3000, 31.3333};
            case "MATABELELAND_NORTH" -> new double[]{-18.5333, 27.5500};
            case "MATABELELAND_SOUTH" -> new double[]{-21.0000, 29.0000};
            case "MIDLANDS" -> new double[]{-19.4500, 29.8167};
            case "MASVINGO" -> new double[]{-20.0833, 30.8333};
            default -> new double[]{-19.0154, 29.1549}; // Fallback
        };
    }

    private double getZimbabweRegionalRainfall(String province) {
        if (province == null || province.trim().isEmpty()) {
            return 500.0; // Default safe middle-ground
        }

        String formattedProvince = province.trim().toUpperCase().replace(" ", "_");

        return switch (formattedProvince) {
            // REGION 1: > 1000mm (Very High Rainfall)
            case "MANICALAND" -> 1000.0 + (Math.random() * 200);

            // REGION 2: 750mm - 1000mm (High Rainfall)
            case "MASHONALAND_WEST", "MASHONALAND_EAST", "MASHONALAND_CENTRAL", "HARARE"
                    -> 750.0 + (Math.random() * 200);

            // REGION 3 & 4: 450mm - 700mm (Medium/Low Rainfall)
            case "MIDLANDS", "MASVINGO"
                    -> 550.0 + (Math.random() * 150);

            // REGION 5: < 450mm (Arid/Very Low Rainfall)
            case "MATABELELAND_NORTH", "MATABELELAND_SOUTH", "BULAWAYO"
                    -> 350.0 + (Math.random() * 150);

            default -> 500.0;
        };
    }

    private double getPredictedYield(double farmSizeHectares, String cropType) {
        if (cropType == null) return farmSizeHectares * 2.0;

        return switch (cropType.toUpperCase()) {
            case "MAIZE" -> farmSizeHectares * 3.5;
            case "TOBACCO" -> farmSizeHectares * 2.2;
            case "WHEAT" -> farmSizeHectares * 4.0;
            case "COTTON" -> farmSizeHectares * 1.2;
            case "SORGHUM", "MILLET" -> farmSizeHectares * 1.5;
            default -> farmSizeHectares * 2.0;
        };
    }
}