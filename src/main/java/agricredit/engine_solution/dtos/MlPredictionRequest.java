package agricredit.engine_solution.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data // This automatically generates ALL getters, setters, toString, and equals/hashCode!
@NoArgsConstructor // Generates an empty constructor
@AllArgsConstructor // Generates a constructor with all fields
public class MlPredictionRequest {
    private double ndviScore;
    private double historicalRainfall;
    private double farmSizeHectares;
    private double historicalYield;
}