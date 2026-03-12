package agricredit.engine_solution.controller;

import agricredit.engine_solution.dtos.AuthRequest;
import agricredit.engine_solution.dtos.AuthResponse;
import agricredit.engine_solution.dtos.PasswordResetRequest;
import agricredit.engine_solution.entity.User;
import agricredit.engine_solution.repository.UserRepository;
import agricredit.engine_solution.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired private UserRepository userRepository;
    @Autowired private JwtService jwtService;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthRequest authRequest) {
        // 1. Check if user exists
        User user = userRepository.findByNationalId(authRequest.getNationalId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Check if account is already locked (3 or more failed attempts)
        if (user.getFailedLoginAttempts() >= 3) {
            return ResponseEntity.status(403).body("ACCOUNT_LOCKED_RESET_REQUIRED");
        }

        // 3. Verify password
        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {

            // Increment the fail counter and save to database
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            userRepository.save(user);

            // Check if this was their 3rd strike
            if (user.getFailedLoginAttempts() >= 3) {
                return ResponseEntity.status(403).body("ACCOUNT_LOCKED_RESET_REQUIRED");
            }

            // Tell them how many tries they have left
            int attemptsLeft = 3 - user.getFailedLoginAttempts();
            return ResponseEntity.status(401).body("Invalid credentials. Attempts remaining: " + attemptsLeft);
        }

        // 4. Success! Reset failed attempts back to 0
        user.setFailedLoginAttempts(0);
        userRepository.save(user);

        // 5. Generate token
        String token = jwtService.generateToken(user.getNationalId());
        return ResponseEntity.ok(new AuthResponse(token, user.getNationalId()));
    }

    // --- NEW ENDPOINT: FORGOT/RESET PASSWORD ---
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {

        // 1. Find the user
        User user = userRepository.findByNationalId(request.getNationalId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Verify their identity using their registered phone number
        if (user.getPhoneNumber() == null || !user.getPhoneNumber().equals(request.getPhoneNumber())) {
            return ResponseEntity.status(401).body("Verification failed: Phone number doesn't match our records.");
        }

        // 3. Update to the new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // 4. Unlock the account!
        user.setFailedLoginAttempts(0);
        userRepository.save(user);

        return ResponseEntity.ok("Password successfully reset! You can now log in with your new password.");
    }
}