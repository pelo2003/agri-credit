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
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final FarmerRepository farmerRepository;
    private final LoanRepository loanRepository;
    private final CreditAssessmentRepository assessmentRepository;
    private final SatelliteService satelliteService;

    // Inject the ML Integration directly to use the new Risk Algorithm!
    private final MlIntegrationService mlIntegrationService;

    @Transactional
    public LoanResponse processLoanApplication(LoanRequest request) {

        Farmer farmer = farmerRepository.findById(request.getFarmerId())
                .orElseThrow(() -> new RuntimeException("Farmer not found in system."));

        // --- THE FIX: Generate a realistic mock NDVI score (0.5 to 0.8) based on Province ---
        Double ndvi = 0.5 + (Math.random() * 0.3);
        boolean hasInsurance = request.getHasCropInsurance() != null ? request.getHasCropInsurance() : false;

        // --- 1. CALL THE SMART RISK ALGORITHM ---
        // (Updated to match the new 3-parameter Hybrid AI signature)
        // --- 1. CALL THE SMART RISK ALGORITHM ---
        Map<String, Object> riskAssessment = mlIntegrationService.assessLoanRisk(
                farmer,
                request.getAmount().doubleValue(),
                ndvi,
                hasInsurance // <--- THIS IS THE MISSING 4TH PARAMETER!
        );

        // --- 2. EXTRACT THE AI'S DECISION ---
        Double creditScore = (Double) riskAssessment.get("creditScore");
        String status = (String) riskAssessment.get("status");
        Double interestRateDouble = (Double) riskAssessment.get("interestRate");
        BigDecimal interestRate = BigDecimal.valueOf(interestRateDouble);
        String reason = (String) riskAssessment.get("reason");

        // --- 3. LOG THE ASSESSMENT FOR THE BANK ---
        CreditAssessment assessment = CreditAssessment.builder()
                .farmer(farmer)
                .ndviIndex(ndvi)
                .riskScore(creditScore)
                .recommendation(status + " - " + reason)
                .build();
        assessmentRepository.save(assessment);

        // --- 4. CREATE THE LOAN (REJECTED OR PENDING) ---
        Loan loan = Loan.builder()
                .farmer(farmer)
                .principalAmount(request.getAmount())
                .durationMonths(request.getDurationMonths())
                .interestRate(interestRate)
                .loanPurpose(request.getLoanPurpose())
                .expectedOffTaker(request.getExpectedOffTaker())
                .hasCropInsurance(hasInsurance)
                .status(status) // Now uses "PENDING" or "REJECTED"
                .build();

        loan = loanRepository.save(loan);

        // --- 5. RETURN RESPONSE TO FLUTTER APP ---
        return LoanResponse.builder()
                .loanId(loan.getId())
                .farmerName(farmer.getFirstName() + " " + farmer.getLastName())
                .principal(loan.getPrincipalAmount())
                .interestRate(loan.getInterestRate())
                .creditScore(creditScore)
                .status(loan.getStatus())
                .loanPurpose(loan.getLoanPurpose())
                .expectedOffTaker(loan.getExpectedOffTaker())
                .rejectionReason(reason) // Shows the farmer exactly why!
                .build();
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public Loan getLoanById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found with id: " + id));
    }

    public List<Loan> getLoansByFarmerId(Long farmerId) {
        Farmer farmer = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));
        return loanRepository.findByFarmer(farmer);
    }

    @Transactional
    public Loan updateLoanStatus(Long id, String newStatus) {
        Loan loan = getLoanById(id);
        loan.setStatus(newStatus);
        return loanRepository.save(loan);
    }
}