package agricredit.engine_solution.repository;

import agricredit.engine_solution.entity.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FarmerRepository extends JpaRepository<Farmer, Long> {

    // Find a farmer by their National ID
    Optional<Farmer> findByNationalId(String nationalId);

    // If you want to find all farmers in a specific province now, you can just use this!
    List<Farmer> findByProvince(String province);
}