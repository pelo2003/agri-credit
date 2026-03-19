package agricredit.engine_solution.service;

import agricredit.engine_solution.dtos.FarmerRequest;
import agricredit.engine_solution.dtos.FarmerResponse;
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
    public FarmerResponse createFarmer(FarmerRequest request) {
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

        User user = new User();
        user.setNationalId(request.getNationalId());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPin()));
        user.setRole("ROLE_FARMER");
        user.setFarmer(savedFarmer);
        userRepository.save(user);

        return toResponse(savedFarmer);
    }

    public List<FarmerResponse> getAllFarmers() {
        return farmerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public FarmerResponse getFarmerById(Long id) {
        Farmer farmer = farmerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Farmer not found with id: " + id));
        return toResponse(farmer);
    }

    @Transactional
    public FarmerResponse updateFarmer(Long id, FarmerRequest request) {
        Farmer existingFarmer = farmerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Farmer not found with id: " + id));

        existingFarmer.setFirstName(request.getFirstName());
        existingFarmer.setLastName(request.getLastName());
        existingFarmer.setPhoneNumber(request.getPhoneNumber());
        existingFarmer.setPrimaryCrop(request.getPrimaryCrop());
        existingFarmer.setFarmSizeHectares(request.getFarmSizeHectares());
        existingFarmer.setLandOwnershipType(request.getLandOwnershipType());
        existingFarmer.setFarmingExperienceYears(request.getFarmingExperienceYears());
        existingFarmer.setProvince(request.getProvince());
        existingFarmer.setDistrict(request.getDistrict());

        return toResponse(farmerRepository.save(existingFarmer));
    }

    // =========================================================================
    // RESPONSE MAPPER — converts Farmer entity → clean FarmerResponse DTO
    // =========================================================================

    private FarmerResponse toResponse(Farmer farmer) {
        double[] coords = getProvinceCoordinates(farmer.getProvince());

        return FarmerResponse.builder()
                .id(farmer.getId())
                .firstName(farmer.getFirstName())
                .lastName(farmer.getLastName())
                .nationalId(farmer.getNationalId())
                .phoneNumber(farmer.getPhoneNumber())
                .primaryCrop(farmer.getPrimaryCrop())
                .farmSizeHectares(farmer.getFarmSizeHectares())
                .province(farmer.getProvince())
                .district(farmer.getDistrict())
                .landOwnershipType(farmer.getLandOwnershipType())
                .farmingExperienceYears(farmer.getFarmingExperienceYears())
                .registeredAt(farmer.getRegisteredAt())
                .farmLocation(FarmerResponse.FarmLocationDTO.builder()
                        .latitude(coords[0])
                        .longitude(coords[1])
                        .province(farmer.getProvince())
                        .district(farmer.getDistrict())
                        .build())
                .build();
    }

    // =========================================================================
    // SINGLE SOURCE OF TRUTH for Zimbabwe province coordinates
    // Also used by MlIntegrationService — consider extracting to a @Component
    // =========================================================================

    public double[] getProvinceCoordinates(String province) {
        if (province == null || province.trim().isEmpty()) {
            return new double[]{-19.0154, 29.1549}; // Center of Zimbabwe
        }

        return switch (province.trim().toUpperCase().replace(" ", "_")) {
            case "HARARE"              -> new double[]{-17.8248, 31.0530};
            case "BULAWAYO"            -> new double[]{-20.1500, 28.5833};
            case "MANICALAND"          -> new double[]{-18.9728, 32.6694};
            case "MASHONALAND_WEST"    -> new double[]{-17.4851, 29.7889};
            case "MASHONALAND_EAST"    -> new double[]{-18.7333, 31.8333};
            case "MASHONALAND_CENTRAL" -> new double[]{-17.3000, 31.3333};
            case "MATABELELAND_NORTH"  -> new double[]{-18.5333, 27.5500};
            case "MATABELELAND_SOUTH"  -> new double[]{-21.0000, 29.0000};
            case "MIDLANDS"            -> new double[]{-19.4500, 29.8167};
            case "MASVINGO"            -> new double[]{-20.0833, 30.8333};
            default                    -> new double[]{-19.0154, 29.1549};
        };
    }
}