package agricredit.engine_solution.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "credit_assessments")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "farmer_id")
    private Farmer farmer;

    // The NDVI (Normalized Difference Vegetation Index) value from your AI model
    private Double ndviIndex;

    // The final credit score (0-100) calculated by your algorithm
    private Double riskScore;

    private String recommendation;

    private LocalDateTime assessmentDate;

    @PrePersist
    protected void onCreate() {
        this.assessmentDate = LocalDateTime.now();
    }
}
