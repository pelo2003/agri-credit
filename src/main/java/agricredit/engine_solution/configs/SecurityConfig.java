package agricredit.engine_solution.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                // --- CORS CONFIGURATION ---
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOriginPatterns(List.of("*")); // Allow all origins for the hackathon
                    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    configuration.setAllowedHeaders(List.of("*"));
                    return configuration;
                }))

                .authorizeHttpRequests(auth -> auth
                        // --- EXPLICITLY ALLOW PRE-FLIGHT REQUESTS ---
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // --- ALLOW EVERYTHING IN THE AUTH FOLDER (Login & Reset) ---
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // --- ALLOW FARMER REGISTRATION ---
                        .requestMatchers(HttpMethod.POST, "/api/v1/farmers").permitAll()

                        // =========================================================
                        // THE DEMO DAY HACK: ALLOW ALL LOAN REQUESTS WITHOUT A TOKEN
                        // =========================================================
                        .requestMatchers("/api/v1/loans/**").permitAll()

                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}