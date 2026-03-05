package agricredit.engine_solution.controller;

import agricredit.engine_solution.dtos.AuthRequest;
import agricredit.engine_solution.dtos.AuthResponse;
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

        // 2. Verify password
        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        // 3. Generate token
        String token = jwtService.generateToken(user.getNationalId());
        return ResponseEntity.ok(new AuthResponse(token, user.getNationalId()));
    }
}