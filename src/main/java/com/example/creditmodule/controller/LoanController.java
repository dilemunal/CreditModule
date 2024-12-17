package com.example.creditmodule.controller;

import com.example.creditmodule.dto.request.CreateLoanRequestDTO;
import com.example.creditmodule.dto.request.ListLoansRequestDTO;
import com.example.creditmodule.dto.response.LoanInstallmentResponseDTO;
import com.example.creditmodule.dto.response.LoanResponseDTO;
import com.example.creditmodule.entity.Loan;
import com.example.creditmodule.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("api/loan/")
public class LoanController {

    @Autowired
    LoanService loanService;

    @PostMapping("createLoan")
    public ResponseEntity<Loan> createLoan(@Valid @RequestBody CreateLoanRequestDTO loanRequestDTO) {
        try {
            Loan loan = loanService.createLoan(loanRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(loan);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("listLoans")
    public ResponseEntity<List<LoanResponseDTO>> listLoans(@Valid @RequestBody ListLoansRequestDTO listLoansRequestDTO) {
        List<LoanResponseDTO> loans = loanService.listLoans(listLoansRequestDTO);
        return ResponseEntity.ok(loans);
    }

    @GetMapping("listInstallments")
    public ResponseEntity<List<LoanInstallmentResponseDTO>> listInstallments(@RequestParam Long loanId) {
        List<LoanInstallmentResponseDTO> loanInstallments = loanService.listInstallments(loanId);
        return ResponseEntity.ok(loanInstallments);
    }

}
