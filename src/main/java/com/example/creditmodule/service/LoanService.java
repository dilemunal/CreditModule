package com.example.creditmodule.service;

import com.example.creditmodule.dto.request.CreateLoanRequestDTO;
import com.example.creditmodule.entity.Loan;

public interface LoanService {

    Loan createLoan(CreateLoanRequestDTO loanRequestDTO);

}
