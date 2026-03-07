package agricredit.engine_solution.entity;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;
import java.time.LocalDateTime;

@Entity
@Table(name = "farmers")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Farmer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String nationalId;

    private String phoneNumber;

    private String primaryCrop;

    private Double farmSizeHectares;

    // The core PostGIS integration: SRID 4326 is standard GPS (WGS84)
    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point farmLocation;

    // --- NEW REAL-WORLD BANKING FIELDS ---

    @Column(name = "land_ownership_type")
    private String landOwnershipType; // e.g., "99-Year Lease", "Offer Letter"

    @Column(name = "farming_experience_years")
    private Integer farmingExperienceYears; // e.g., 5, 10

    private LocalDateTime registeredAt;

    @PrePersist
    protected void onCreate() {
        this.registeredAt = LocalDateTime.now();
    }
}