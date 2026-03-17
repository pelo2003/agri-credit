package agricredit.engine_solution.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // MADE UNIQUE: Phone number is now the primary login credential
    @Column(unique = true, nullable = false)
    private String phoneNumber;

    @Column(columnDefinition = "integer default 0")
    private int failedLoginAttempts = 0;

    // National ID is still stored, but no longer the login username
    @Column(unique = true, nullable = false)
    private String nationalId;

    @Column(nullable = false)
    private String password; // This will store the BCrypt hashed PIN

    private String role;

    @OneToOne
    @JoinColumn(name = "farmer_id")
    private Farmer farmer;
}