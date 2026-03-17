package agricredit.engine_solution.service;

import agricredit.engine_solution.dtos.FarmerRequest;
import agricredit.engine_solution.entity.Farmer;
import agricredit.engine_solution.entity.User;
import agricredit.engine_solution.repository.FarmerRepository;
import agricredit.engine_solution.repository.UserRepository;
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

        farmer.setProvince(request.getProvince());
        farmer.setDistrict(request.getDistrict());

        farmer.setRegisteredAt(LocalDateTime.now());
        Farmer savedFarmer = farmerRepository.save(farmer);

        // Create the user login credentials automatically
        User user = new User();
        user.setNationalId(request.getNationalId());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPin()));
        user.setRole("ROLE_FARMER");

        // Link the user to the farmer profile
        user.setFarmer(savedFarmer);

        // --- THE CRITICAL FIX: You must save the user! ---
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

        // --- MINOR FIX: Don't forget to update the district too ---
        existingFarmer.setDistrict(request.getDistrict());

        return farmerRepository.save(existingFarmer);
    }
}