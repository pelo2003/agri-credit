package agricredit.engine_solution.controller;

import agricredit.engine_solution.dtos.FarmerRequest;
import agricredit.engine_solution.entity.Farmer;
import agricredit.engine_solution.service.FarmerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/farmers")
@RequiredArgsConstructor
public class FarmerController {

    private final FarmerService farmerService;

    @PostMapping
    public ResponseEntity<Farmer> createFarmer(@RequestBody FarmerRequest request) {
        return ResponseEntity.ok(farmerService.createFarmer(request));
    }

    @GetMapping
    public ResponseEntity<List<Farmer>> getAllFarmers() {
        return ResponseEntity.ok(farmerService.getAllFarmers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Farmer> getFarmerById(@PathVariable Long id) {
        return ResponseEntity.ok(farmerService.getFarmerById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Farmer> updateFarmer(@PathVariable Long id, @RequestBody FarmerRequest request) {
        return ResponseEntity.ok(farmerService.updateFarmer(id, request));
    }
}