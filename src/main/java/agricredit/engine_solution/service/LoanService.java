package agricredit.engine_solution.service;

import agricredit.engine_solution.dtos.LoanRequest;
import agricredit.engine_solution.dtos.LoanResponse;
import agricredit.engine_solution.entity.CreditAssessment;
import agricredit.engine_solution.entity.Farmer;
import agricredit.engine_solution.entity.Loan;
import agricredit.engine_solution.repository.CreditAssessmentRepository;
import agricredit.engine_solution.repository.FarmerRepository;
import agricredit.engine_solution.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor // Lombok generates the constructor for injected fields automatically!
public class LoanService {

    // Using constructor injection via Lombok fixes those yellow "never assigned" warnings
    private final FarmerRepository farmerRepository;
    private final LoanRepository loanRepository;
    private final CreditAssessmentRepository assessmentRepository;
    private final SatelliteService satelliteService;
    private final CreditService creditService;

    @Transactional
    public LoanResponse processLoanApplication(LoanRequest request) {

        // 1. Identify the Farmer
        Farmer farmer = farmerRepository.findById(request.getFarmerId())
                .orElseThrow(() -> new RuntimeException("Farmer not found in system."));

        // 2. Fetch "Space" Data
        Double ndvi = satelliteService.fetchNdviIndex(farmer.getFarmLocation());

        // 3. Crunch the Financial Numbers using the new AI Engine!
        // NOTE: We are using some placeholder values (100.0, 5.0, 2.0) for weather/yield
        // until you connect those to actual database fields or APIs.
        BigDecimal interestRate = creditService.processAiCreditAssessment(
                ndvi,
                100.0, // Mock historical rainfall
                5.0,   // Mock farm size in hectares
                2.0,   // Mock historical yield
                farmer.getPrimaryCrop()
        );

        // For logging purposes, let's derive a basic risk score based on the interest rate
        // (Since a 5% rate = 100 score, and 25% rate = 0 score based on your formula)
        double derivedRiskScore = (25.0 - interestRate.doubleValue()) / 0.2;

        // 4. Log the AI Assessment for Audit Purposes
        CreditAssessment assessment = CreditAssessment.builder()
                .farmer(farmer)
                .ndviIndex(ndvi)
                .riskScore(derivedRiskScore)
                .recommendation(derivedRiskScore > 50 ? "APPROVE" : "REJECT - HIGH RISK")
                .build();
        assessmentRepository.save(assessment);

        // 5. Issue the Loan Record
        Loan loan = new Loan();
        loan.setFarmer(farmer);
        loan.setPrincipalAmount(request.getAmount());
        loan.setDurationMonths(request.getDurationMonths());
        loan.setInterestRate(interestRate);
        loan.setStatus(derivedRiskScore > 50 ? "APPROVED" : "REJECTED");
        loan = loanRepository.save(loan);

        // 6. Return a Clean Response to the Flutter App
        return LoanResponse.builder()
                .loanId(loan.getId())
                .farmerName(farmer.getFirstName() + " " + farmer.getLastName())
                .principal(loan.getPrincipalAmount())
                .interestRate(loan.getInterestRate())
                .creditScore(derivedRiskScore)
                .status(loan.getStatus())
                .build();
    }
    // ... (Keep your existing processLoanApplication method above this) ...

    // --- READ (ALL) ---
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    // --- READ (BY ID) ---
    public Loan getLoanById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found with id: " + id));
    }

    // --- READ (BY FARMER ID) ---
    // (This is highly recommended so a farmer only sees their own loans!)
    public List<Loan> getLoansByFarmerId(Long farmerId) {
        Farmer farmer = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));
        return loanRepository.findByFarmer(farmer);
        // Note: You may need to add List<Loan> findByFarmer(Farmer farmer); to your LoanRepository!
    }

    // --- UPDATE (Status Only) ---
    @Transactional
    public Loan updateLoanStatus(Long id, String newStatus) {
        Loan loan = getLoanById(id);
        loan.setStatus(newStatus);
        return loanRepository.save(loan);
    }

}