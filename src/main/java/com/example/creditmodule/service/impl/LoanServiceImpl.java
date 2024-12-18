package com.example.creditmodule.service.impl;

import com.example.creditmodule.dto.request.CreateLoanRequestDTO;
import com.example.creditmodule.dto.request.ListLoansRequestDTO;
import com.example.creditmodule.dto.request.PayLoanRequest;
import com.example.creditmodule.dto.response.LoanInstallmentResponseDTO;
import com.example.creditmodule.dto.response.LoanPaymentResponseDTO;
import com.example.creditmodule.dto.response.LoanResponseDTO;
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
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

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

        double totalAmount = loanRequestDTO.getLoanAmount() * (loanRequestDTO.getInterestRate());

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

    @Override
    public List<LoanResponseDTO> listLoans(ListLoansRequestDTO listLoansRequestDTO) {
        if(!customerRepository.findById(listLoansRequestDTO.getCustomerId()).isPresent()){
            throw new CreditModuleException(ErrorMessage.CUSTOMER_NOT_FOUND);
        }
        List<Loan> loans = loanRepository.findByCustomerId(listLoansRequestDTO.getCustomerId());

        if(ObjectUtils.isEmpty(loans)) {
            throw new CreditModuleException(ErrorMessage.LOAN_NOT_FOUND);
        }

        if (listLoansRequestDTO.getNumberOfInstallment() != null) {
            loans = loans.stream()
                    .filter(loan -> loan.getNumberOfInstallment().equals(listLoansRequestDTO.getNumberOfInstallment()))
                    .collect(Collectors.toList());
        }

        if (listLoansRequestDTO.getIsPaid() != null) {
            loans = loans.stream()
                    .filter(loan -> loan.getIsPaid().equals(listLoansRequestDTO.getIsPaid()))
                    .collect(Collectors.toList());
        }

        return loans.stream()
                .map(loan -> new LoanResponseDTO(
                        loan.getId(),
                        loan.getLoanAmount(),
                        loan.getNumberOfInstallment(),
                        loan.getCreateDate(),
                        loan.getIsPaid()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<LoanInstallmentResponseDTO> listInstallments(Long loanId) {
        List<LoanInstallment> installments = loanInstallmentRepository.findByLoanId(loanId);
        if(ObjectUtils.isEmpty(installments)){
            throw new CreditModuleException(ErrorMessage.INSTALLMENT_NOT_FOUND);
        }
        return installments.stream().map(
                loanInstallment -> new LoanInstallmentResponseDTO(
                        loanInstallment.getId(),
                        loanInstallment.getAmount(),
                        loanInstallment.getPaidAmount(),
                        loanInstallment.getDueDate(),
                        loanInstallment.getPaymentDate(),
                        loanInstallment.getIsPaid())
        ).collect(Collectors.toList());
    }

    @Override
    public LoanPaymentResponseDTO payLoan(PayLoanRequest payLoanRequestDTO) {
        LocalDate today = LocalDate.now();
        //check if loan exists
        Loan loan = loanRepository.findById(payLoanRequestDTO.getLoanId()).orElseThrow(
                () -> new CreditModuleException(ErrorMessage.LOAN_NOT_FOUND));

        //check if payable in terms of month and isPaid
        List<LoanInstallment> payableInstallments = loanInstallmentRepository.findByLoanId(payLoanRequestDTO.getLoanId())
                .stream()
                .filter(installment -> !installment.getIsPaid() && installment.getDueDate().isBefore(today.plusMonths(3)))
                .toList();

        if (payableInstallments.isEmpty()) {
            throw new CreditModuleException(ErrorMessage.NO_PAYABLE_INSTALLMENTS);
        }

        double remainingAmount = payLoanRequestDTO.getPaymentAmount();
        int paidCount = 0;
        for (LoanInstallment installment : payableInstallments) {Double extra = 0.0;  //discount - penalty amount
            long daysDifference = DAYS.between(today, installment.getDueDate());
            if (today.isBefore(installment.getDueDate())) {
                extra = - (installment.getAmount() * 0.001 * daysDifference);
            }
            else if (today.isAfter(installment.getDueDate())) {
                extra = installment.getAmount() * 0.001 * daysDifference;  // Penalty
            }

            Double finalInstallmentAmount = installment.getAmount() + extra;
            if (remainingAmount >= finalInstallmentAmount) {
                installment.setPaidAmount(finalInstallmentAmount);
                installment.setIsPaid(true);
                installment.setPaymentDate(today);
                //extra money from user
                remainingAmount -= finalInstallmentAmount;
                loanInstallmentRepository.save(installment);
                paidCount++;
            }
        }

        //if all installments is paid, update
        boolean allInstallmentsPaid = payableInstallments.stream().allMatch(LoanInstallment::getIsPaid);
        if (allInstallmentsPaid) {
            loan.setIsPaid(true);
            loanRepository.save(loan);
            loan.getCustomer().setUsedCreditLimit(
                    loan.getCustomer().getUsedCreditLimit() - loan.getLoanAmount()
            );
            customerRepository.save(loan.getCustomer());
        }

        int totalInstallments = loan.getNumberOfInstallment();
        Long unpaidInstallmentsCount =  loanInstallmentRepository.findByLoanId(loan.getId())
                .stream()
                .filter(installment -> !installment.getIsPaid())
                .count();

        return new LoanPaymentResponseDTO(
                loan.getId(),
                loan.getLoanAmount(),
                totalInstallments,
                paidCount,
                unpaidInstallmentsCount,
                remainingAmount,
                today
        );
    }

}



