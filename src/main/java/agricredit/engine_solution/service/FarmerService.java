package agricredit.engine_solution.service;

import agricredit.engine_solution.dtos.FarmerRequest;
import agricredit.engine_solution.entity.Farmer;
import agricredit.engine_solution.entity.User;
import agricredit.engine_solution.repository.FarmerRepository;
import agricredit.engine_solution.repository.UserRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FarmerService {

    private final FarmerRepository farmerRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public Farmer createFarmer(FarmerRequest request) {
        Farmer farmer = new Farmer();
        farmer.setFirstName(request.getFirstName());
        farmer.setLastName(request.getLastName());
        farmer.setNationalId(request.getNationalId());
        farmer.setPhoneNumber(request.getPhoneNumber());
        farmer.setPrimaryCrop(request.getPrimaryCrop());
        farmer.setFarmSizeHectares(request.getFarmSizeHectares());

        farmer.setLandOwnershipType(request.getLandOwnershipType());
        farmer.setFarmingExperienceYears(request.getFarmingExperienceYears());
        // This takes "HARARE" and turns it into the long geometry string!
        farmer.setFarmLocation(generateLocationFromProvince(request.getProvince()));

        farmer.setProvince(request.getProvince());
        farmer.setDistrict(request.getDistrict());

        farmer.setRegisteredAt(LocalDateTime.now());
        Farmer savedFarmer = farmerRepository.save(farmer);

        // Create the user login credentials automatically
        User user = new User();
        user.setNationalId(request.getNationalId());
        user.setPhoneNumber(request.getPhoneNumber());

        // FIX: use pin instead of password
        user.setPassword(passwordEncoder.encode(request.getPin()));
        user.setRole("ROLE_FARMER");

        // Link the user to the farmer profile
        user.setFarmer(savedFarmer);

        userRepository.save(user);

        return savedFarmer;
    }

    public List<Farmer> getAllFarmers() {
        return farmerRepository.findAll();
    }

    public Farmer getFarmerById(Long id) {
        return farmerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Farmer not found with id: " + id));
    }

    @Transactional
    public Farmer updateFarmer(Long id, FarmerRequest request) {
        Farmer existingFarmer = getFarmerById(id);

        existingFarmer.setFirstName(request.getFirstName());
        existingFarmer.setLastName(request.getLastName());
        existingFarmer.setPhoneNumber(request.getPhoneNumber());
        existingFarmer.setPrimaryCrop(request.getPrimaryCrop());
        existingFarmer.setFarmSizeHectares(request.getFarmSizeHectares());

        existingFarmer.setLandOwnershipType(request.getLandOwnershipType());
        existingFarmer.setFarmingExperienceYears(request.getFarmingExperienceYears());

        existingFarmer.setProvince(request.getProvince());
        existingFarmer.setDistrict(request.getDistrict());

        return farmerRepository.save(existingFarmer);
    }
    // --- NEW HELPER METHOD FOR MAP LOCATION ---
    private Point generateLocationFromProvince(String province) {
        if (province == null || province.trim().isEmpty()) {
            return null;
        }

        double lat = 0.0;
        double lon = 0.0;

        // Match the province to standard GPS coordinates
        switch (province.toUpperCase()) {
            case "HARARE": lat = -17.82; lon = 31.05; break;
            case "BULAWAYO": lat = -20.15; lon = 28.58; break;
            case "MANICALAND": lat = -18.97; lon = 32.67; break;
            case "MASHONALAND CENTRAL": lat = -17.30; lon = 31.33; break;
            case "MASHONALAND EAST": lat = -18.18; lon = 31.55; break;
            case "MASHONALAND WEST": lat = -17.36; lon = 30.20; break;
            case "MASVINGO": lat = -20.07; lon = 30.83; break;
            case "MATABELELAND NORTH": lat = -18.93; lon = 27.80; break;
            case "MATABELELAND SOUTH": lat = -20.93; lon = 29.00; break;
            case "MIDLANDS": lat = -19.45; lon = 29.81; break;
            default:
                // Default to center of Zimbabwe if not matched exactly
                lat = -19.0154; lon = 29.1549; break;
        }

        // Create the geometry point (SRID 4326 is standard GPS)
        // Note: Map coordinates are always (Longitude, Latitude)
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        return geometryFactory.createPoint(new Coordinate(lon, lat));
    }
}