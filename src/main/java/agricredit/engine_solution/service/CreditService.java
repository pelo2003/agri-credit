package agricredit.engine_solution.service;

import agricredit.engine_solution.dtos.MlPredictionRequest;
import agricredit.engine_solution.dtos.MlPredictionResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CreditService {

    private final MlIntegrationService mlIntegrationService;

    // 1. Inject the ML Service via constructor
    public CreditService(MlIntegrationService mlIntegrationService) {
        this.mlIntegrationService = mlIntegrationService;
    }

    // 2. The Main AI-Driven Assessment Method
    public BigDecimal processAiCreditAssessment(
            double ndviIndex,
            double historicalRainfall,
            double farmSizeHectares,
            double historicalYield,
            String cropType) {

        // Step A: Package the data for the Python Model
        MlPredictionRequest mlRequest = new MlPredictionRequest();
        mlRequest.setNdviScore(ndviIndex);
        mlRequest.setHistoricalRainfall(historicalRainfall);
        mlRequest.setFarmSizeHectares(farmSizeHectares);
        mlRequest.setHistoricalYield(historicalYield);

        // Step B: Call the Python API to get the AI prediction
        MlPredictionResponse aiResponse = mlIntegrationService.getCreditScoreFromModel(mlRequest);

        // Step C: You can blend your existing rules with the AI score
        // For example, if the AI gives a score out of 100, we can still apply the crop bonus
        double finalRiskScore = applyCropTypeBonus(aiResponse.getPredictedCreditScore(), cropType);

        // Step D: Calculate and return the final interest rate
        return calculateInterestRate(finalRiskScore);
    }

    // --- Your Existing Helper Methods ---

    // Modified slightly to act as a bonus modifier for the AI score
    private Double applyCropTypeBonus(Double aiScore, String cropType) {
        double adjustedScore = aiScore;

        // Fintech rule: Drought-resistant crops get a slight risk reduction/score boost
        if ("Sorghum".equalsIgnoreCase(cropType) || "Cassava".equalsIgnoreCase(cropType)) {
            adjustedScore += 5.0;
        }

        return Math.min(Math.max(adjustedScore, 0.0), 100.0); // Ensure it stays between 0-100
    }

    // Dynamic Interest Rate: Better crop health/AI score = lower risk = cheaper loan
    public BigDecimal calculateInterestRate(Double riskScore) {
        double rate = 25.0 - (riskScore * 0.2); // Formula: 25% max, reduces as score goes up
        rate = Math.max(rate, 5.0); // Absolute minimum interest rate of 5%

        return BigDecimal.valueOf(rate).setScale(2, RoundingMode.HALF_UP);
    }
}