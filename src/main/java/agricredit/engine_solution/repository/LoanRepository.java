package agricredit.engine_solution.repository;

import agricredit.engine_solution.entity.Farmer;
import agricredit.engine_solution.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    // Add this line! Spring Boot will automatically write the SQL for it.
    List<Loan> findByFarmer(Farmer farmer);

}