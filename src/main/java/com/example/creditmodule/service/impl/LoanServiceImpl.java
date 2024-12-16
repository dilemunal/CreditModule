package com.example.creditmodule.service.impl;

import com.example.creditmodule.dto.request.CreateLoanRequestDTO;
import com.example.creditmodule.entity.Customer;
import com.example.creditmodule.entity.Loan;
import com.example.creditmodule.entity.LoanInstallment;
import com.example.creditmodule.enums.ErrorMessage;
import com.example.creditmodule.exception.CreditModuleException;
import com.example.creditmodule.repository.CustomerRepository;
import com.example.creditmodule.repository.LoanInstallmentRepository;
import com.example.creditmodule.repository.LoanRepository;
import com.example.creditmodule.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanServiceImpl implements LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private LoanInstallmentRepository loanInstallmentRepository;

    public Loan createLoan(CreateLoanRequestDTO loanRequestDTO) throws CreditModuleException {
        //check if customer exists
        Customer customer = customerRepository.findById(loanRequestDTO.getCustomerId())
                .orElseThrow(() -> new CreditModuleException(ErrorMessage.CUSTOMER_NOT_FOUND));

        // check if customer has enough limits
        if (customer.getCreditLimit() < loanRequestDTO.getLoanAmount()) {
            throw new CreditModuleException(ErrorMessage.INSUFFICIENT_CREDIT_LIMIT);
        }

        if (!isValidInstallmentCount(loanRequestDTO.getNumberOfInstallment())) {
            throw new CreditModuleException(ErrorMessage.INVALID_NUMBER_OF_INSTALLMENTS);
        }

        double totalAmount = loanRequestDTO.getLoanAmount() * (1 + loanRequestDTO.getInterestRate());

        //All installments should have same amount
        double installmentAmount = totalAmount / loanRequestDTO.getNumberOfInstallment();

        Loan loan = new Loan();
        loan.setLoanAmount(loanRequestDTO.getLoanAmount());
        loan.setNumberOfInstallment(loanRequestDTO.getNumberOfInstallment());
        loan.setCreateDate(LocalDate.now());
        loan.setCustomer(customer);
        loan.setIsPaid(false);
        Loan savedLoan = loanRepository.save(loan);

        //update customers credit limit
        customer.setUsedCreditLimit(customer.getUsedCreditLimit() + loanRequestDTO.getLoanAmount());
        customerRepository.save(customer);


        List<LoanInstallment> installments = createInstallments(savedLoan, installmentAmount, loanRequestDTO.getNumberOfInstallment());

        loanInstallmentRepository.saveAll(installments);

        return savedLoan;

    }

    private boolean isValidInstallmentCount(Integer numberOfInstallment) {
        return numberOfInstallment == 6 || numberOfInstallment == 9 || numberOfInstallment == 12 || numberOfInstallment == 24;
    }

    private List<LoanInstallment> createInstallments(Loan loan, double installmentAmount, int numberOfInstallments) {
        List<LoanInstallment> installments = new ArrayList<>();
        //Due Date of Installments should be first day of months
        LocalDate dueDate = LocalDate.now().plusMonths(1).withDayOfMonth(1);
        for (int i = 0; i < numberOfInstallments; i++) {
            LoanInstallment installment = new LoanInstallment();
            installment.setLoan(loan);
            installment.setAmount(installmentAmount);
            installment.setPaidAmount(0.0);
            installment.setDueDate(dueDate);
            installment.setIsPaid(false);

            installments.add(installment);
            // dueDate ++ for next installment
            dueDate = dueDate.plusMonths(1);
        }

        return installments;
    }



}



