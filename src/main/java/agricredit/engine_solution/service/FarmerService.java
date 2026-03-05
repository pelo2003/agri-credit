package agricredit.engine_solution.service;

import agricredit.engine_solution.dtos.FarmerRequest;
import agricredit.engine_solution.entity.Farmer;
import agricredit.engine_solution.entity.User;
import agricredit.engine_solution.repository.FarmerRepository;
import agricredit.engine_solution.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
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
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    // --- CREATE ---
    @Transactional
    public Farmer createFarmer(FarmerRequest request) {
        Farmer farmer = new Farmer();
        farmer.setFirstName(request.getFirstName());
        farmer.setLastName(request.getLastName());
        farmer.setNationalId(request.getNationalId());
        farmer.setPhoneNumber(request.getPhoneNumber());
        farmer.setPrimaryCrop(request.getPrimaryCrop());
        farmer.setFarmSizeHectares(request.getFarmSizeHectares());

        if (request.getLongitude() != null && request.getLatitude() != null) {
            farmer.setFarmLocation(geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude())));
        }
        farmer.setRegisteredAt(LocalDateTime.now());
        Farmer savedFarmer = farmerRepository.save(farmer);

        User user = new User();
        user.setNationalId(request.getNationalId());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_FARMER");
        user.setFarmer(savedFarmer);
        userRepository.save(user);

        return savedFarmer;
    }

    // --- READ (ALL) ---
    public List<Farmer> getAllFarmers() {
        return farmerRepository.findAll();
    }

    // --- READ (BY ID) ---
    public Farmer getFarmerById(Long id) {
        return farmerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Farmer not found with id: " + id));
    }

    // --- UPDATE ---
    @Transactional
    public Farmer updateFarmer(Long id, FarmerRequest request) {
        Farmer existingFarmer = getFarmerById(id);

        existingFarmer.setFirstName(request.getFirstName());
        existingFarmer.setLastName(request.getLastName());
        existingFarmer.setPhoneNumber(request.getPhoneNumber());
        existingFarmer.setPrimaryCrop(request.getPrimaryCrop());
        existingFarmer.setFarmSizeHectares(request.getFarmSizeHectares());

        if (request.getLongitude() != null && request.getLatitude() != null) {
            existingFarmer.setFarmLocation(geometryFactory.createPoint(new Coordinate(request.getLongitude(), request.getLatitude())));
        }

        return farmerRepository.save(existingFarmer);
    }
}