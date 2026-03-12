package agricredit.engine_solution.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phoneNumber; // Used to verify their identity during reset

    @Column(columnDefinition = "integer default 0")
    private int failedLoginAttempts = 0;

    @Column(unique = true, nullable = false)
    private String nationalId; // Used as the username

    @Column(nullable = false)
    private String password; // Will be stored as a BCrypt hash

    private String role; // e.g., "ROLE_FARMER" or "ROLE_AGENT"

    @OneToOne
    @JoinColumn(name = "farmer_id")
    private Farmer farmer; // Links this login account to a physical farmer profile
}