package agricredit.engine_solution.repository;

import agricredit.engine_solution.entity.CreditAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CreditAssessmentRepository extends JpaRepository<CreditAssessment, Long> {

    // Get the latest assessments for a farmer, ordered by date
    List<CreditAssessment> findByFarmerIdOrderByAssessmentDateDesc(Long farmerId);
}
