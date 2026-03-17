package agricredit.engine_solution.repository;

import agricredit.engine_solution.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // You probably already have this one:
    Optional<User> findByNationalId(String nationalId);

    // --- ADD THIS NEW LINE ---
    Optional<User> findByPhoneNumber(String phoneNumber);
}