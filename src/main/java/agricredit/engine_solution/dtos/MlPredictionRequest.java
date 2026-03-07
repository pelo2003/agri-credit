package agricredit.engine_solution.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MlPredictionRequest {
    private double ndviScore;
    private double historicalRainfall;
    private double farmSizeHectares;
    private double historicalYield;

    // --- NEW FIELDS FOR THE AI TO ASSESS RISK ---
    private int farmingExperienceYears; // More experience = lower risk
    private boolean hasCropInsurance;   // Has insurance = much lower risk
}