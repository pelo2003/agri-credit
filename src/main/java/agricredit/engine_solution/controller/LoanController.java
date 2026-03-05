package agricredit.engine_solution.controller;

import agricredit.engine_solution.dtos.LoanRequest;
import agricredit.engine_solution.dtos.LoanResponse;
import agricredit.engine_solution.entity.Loan;
import agricredit.engine_solution.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/apply")
    public ResponseEntity<LoanResponse> applyForLoan(@RequestBody LoanRequest request) {
        return ResponseEntity.ok(loanService.processLoanApplication(request));
    }

    @GetMapping
    public ResponseEntity<List<Loan>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Loan> getLoanById(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.getLoanById(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Loan> updateLoanStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(loanService.updateLoanStatus(id, status));
    }
}