package com.example.creditmodule.service;

import com.example.creditmodule.dto.request.CreateLoanRequestDTO;
import com.example.creditmodule.dto.request.ListLoansRequestDTO;
import com.example.creditmodule.dto.response.LoanResponseDTO;
import com.example.creditmodule.entity.Loan;

import java.util.List;

public interface LoanService {

    Loan createLoan(CreateLoanRequestDTO loanRequestDTO);

    List<LoanResponseDTO> listLoans(ListLoansRequestDTO listLoansRequestDTO);
}
