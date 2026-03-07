package agricredit.engine_solution.service;

import agricredit.engine_solution.entity.Farmer;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class MlIntegrationService {

    public Map<String, Object> assessLoanRisk(Farmer farmer, Double requestedAmount, Double ndviScore) {
        log.info("Analyzing risk for Farmer: {} requesting ${}", farmer.getFirstName(), requestedAmount);

        double creditScore = 100.0; // Start with a perfect score and deduct for risks
        String rejectionReason = "N/A";
        String status = "PENDING"; // Default to PENDING instead of APPROVED
        double interestRate = 12.0;

        // --- THE 5 AI FACTORS ---

        // Mocking the new data points based on GPS location for the Hackathon
        double predictedRainfallMm = 450.0 + (Math.random() * 400); // 450mm to 850mm
        double yieldPredictionTons = farmer.getFarmSizeHectares() * 2.5; // Predict 2.5 tons per hectare
        int transactionHistoryScore = 50 + (int)(Math.random() * 50); // Score out of 100

        // 1. Transaction History Check
        if (transactionHistoryScore < 60) {
            creditScore -= 25;
            if (rejectionReason.equals("N/A")) rejectionReason = "Poor transaction history or previous defaults.";
        }

        // 2. Rainfall Patterns (e.g., Maize needs at least 500mm)
        if (predictedRainfallMm < 500.0) {
            creditScore -= 20;
            if (rejectionReason.equals("N/A")) rejectionReason = "Predicted rainfall (" + Math.round(predictedRainfallMm) + "mm) in your region is too low for a safe harvest.";
        }

        // 3. Crop Health Index (NDVI)
        if (ndviScore < 0.3) {
            creditScore -= 25;
            if (rejectionReason.equals("N/A")) rejectionReason = "Satellite NDVI indicates existing crop failure or severe dry land.";
        }

        // 4. Yield Prediction vs Loan Amount (Farm Capacity)
        // If they ask for more money than their predicted harvest is worth
        double predictedRevenue = yieldPredictionTons * 300; // Assume $300 per ton
        if (requestedAmount > predictedRevenue) {
            creditScore -= 30;
            if (rejectionReason.equals("N/A")) rejectionReason = "Requested loan amount ($" + requestedAmount + ") exceeds predicted harvest revenue ($" + predictedRevenue + ").";
        }

        // 5. Farm Size Baseline Check
        if (farmer.getFarmSizeHectares() < 0.5) {
            creditScore -= 10;
            if (rejectionReason.equals("N/A")) rejectionReason = "Farm size too small for commercial loan viability.";
        }

        // --- THE FINAL DECISION ---
        if (creditScore < 60.0) {
            status = "REJECTED";
            interestRate = 0.0;
            // If somehow no specific reason was caught, provide a general one
            if (rejectionReason.equals("N/A")) {
                rejectionReason = "Overall AI risk profile too high based on combined factors.";
            }
        } else {
            // If they pass, they are NOT approved. They are PENDING.
            status = "PENDING";
            rejectionReason = "Passed AI screening. Awaiting final review by a Loan Officer.";
            interestRate = Math.max(8.0, 20.0 - (creditScore / 10));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("creditScore", Math.min(creditScore, 99.9));
        result.put("status", status);
        result.put("interestRate", interestRate);
        result.put("reason", rejectionReason);

        return result;
    }
}