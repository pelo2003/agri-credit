package agricredit.engine_solution.repository;

import agricredit.engine_solution.entity.Farmer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.locationtech.jts.geom.Geometry;
import java.util.List;
import java.util.Optional;

@Repository
public interface FarmerRepository extends JpaRepository<Farmer, Long> {

    Optional<Farmer> findByNationalId(String nationalId);

    // Spatial Query: Find all farmers within a specific geographic boundary (Polygon)
    @Query("SELECT f FROM Farmer f WHERE within(f.farmLocation, :boundary) = true")
    List<Farmer> findFarmersWithinBoundary(@Param("boundary") Geometry boundary);
}
