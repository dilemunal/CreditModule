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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class LoanServiceImplTest {

        @Mock
        private CustomerRepository customerRepository;

        @Mock
        private LoanRepository loanRepository;

        @Mock
        private LoanInstallmentRepository loanInstallmentRepository;

        @InjectMocks
        private LoanServiceImpl loanService;

        @Test
        void createLoan_shouldCreateLoanSuccessfully() {
            Long customerId = 1L;
            CreateLoanRequestDTO request = new CreateLoanRequestDTO();
            request.setCustomerId(customerId);
            request.setLoanAmount(5000.0);
            request.setNumberOfInstallment(12);
            request.setInterestRate(0.2);

            Customer customer = new Customer();
            customer.setId(customerId);
            customer.setCreditLimit(10000.0);
            customer.setUsedCreditLimit(2000.0);

            Loan loan = new Loan();
            loan.setId(1L);
            loan.setLoanAmount(5000.0);
            loan.setNumberOfInstallment(12);

            Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
            Mockito.when(loanRepository.save(Mockito.any(Loan.class))).thenReturn(loan);

            Loan createdLoan = loanService.createLoan(request);

            Assertions.assertNotNull(createdLoan);
            Assertions.assertEquals(5000.0, createdLoan.getLoanAmount());
            Assertions.assertEquals(12, createdLoan.getNumberOfInstallment());
            Mockito.verify(customerRepository).save(Mockito.any(Customer.class));
            Mockito.verify(loanRepository).save(Mockito.any(Loan.class));
            Mockito.verify(loanInstallmentRepository).saveAll(Mockito.anyList());
        }

        @Test
        void createLoan_shouldThrowExceptionWhenCustomerNotFound() {
            Long customerId = 1L;
            CreateLoanRequestDTO request = new CreateLoanRequestDTO();
            request.setCustomerId(customerId);
            request.setLoanAmount(5000.0);

            Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

            CreditModuleException exception = Assertions.assertThrows(
                    CreditModuleException.class,
                    () -> loanService.createLoan(request)
            );
            Assertions.assertEquals(ErrorMessage.CUSTOMER_NOT_FOUND.getMessage(), exception.getErrorMessage());
        }

        @Test
        void createLoan_shouldThrowExceptionWhenCreditLimitIsInsufficient() {
            Long customerId = 1L;
            CreateLoanRequestDTO request = new CreateLoanRequestDTO();
            request.setCustomerId(customerId);
            request.setLoanAmount(15000.0);
            request.setNumberOfInstallment(12);

            Customer customer = new Customer();
            customer.setId(customerId);
            customer.setCreditLimit(10000.0);
            customer.setUsedCreditLimit(5000.0);

            Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

            CreditModuleException exception = Assertions.assertThrows(
                    CreditModuleException.class,
                    () -> loanService.createLoan(request)
            );
            Assertions.assertEquals(ErrorMessage.INSUFFICIENT_CREDIT_LIMIT.getMessage(), exception.getErrorMessage());
        }

        @Test
        void createLoan_shouldThrowExceptionWhenInstallmentsAreInvalid() {
            Long customerId = 1L;
            CreateLoanRequestDTO request = new CreateLoanRequestDTO();
            request.setCustomerId(customerId);
            request.setLoanAmount(5000.0);
            request.setNumberOfInstallment(5);

            Customer customer = new Customer();
            customer.setId(customerId);
            customer.setCreditLimit(10000.0);
            customer.setUsedCreditLimit(2000.0);

            Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

            CreditModuleException exception = Assertions.assertThrows(
                    CreditModuleException.class,
                    () -> loanService.createLoan(request)
            );
            Assertions.assertEquals(ErrorMessage.INVALID_NUMBER_OF_INSTALLMENTS.getMessage(), exception.getErrorMessage());
        }

    @Test
    void listLoans_ThrowExceptionIfCustomerNotFound() {
        Long customerId = 1000L;
        ListLoansRequestDTO request = new ListLoansRequestDTO();
        request.setCustomerId(customerId);

        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        CreditModuleException exception = Assertions.assertThrows(
                CreditModuleException.class,
                () -> loanService.listLoans(request)
        );

        Assertions.assertEquals(ErrorMessage.CUSTOMER_NOT_FOUND.getMessage(), exception.getErrorMessage());
    }

    @Test
    void listLoans_shouldThrowExceptionIfNoLoansFound() {
        Long customerId = 100L;
        ListLoansRequestDTO request = new ListLoansRequestDTO();
        request.setCustomerId(customerId);

        Customer customer = new Customer();
        customer.setId(customerId);

        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        Mockito.when(loanRepository.findByCustomerId(customerId)).thenReturn(Collections.emptyList());

        CreditModuleException exception = Assertions.assertThrows(
                CreditModuleException.class,
                () -> loanService.listLoans(request)
        );

        Assertions.assertEquals(ErrorMessage.LOAN_NOT_FOUND.getMessage(), exception.getErrorMessage());
    }

    @Test
    void listLoans_shouldApplyFiltersCorrectly() {
        Long customerId = 100L;
        Integer filterInstallments = 12;
        Boolean filterIsPaid = false;

        ListLoansRequestDTO request = new ListLoansRequestDTO();
        request.setCustomerId(customerId);
        request.setNumberOfInstallment(filterInstallments);
        request.setIsPaid(filterIsPaid);

        Customer customer = new Customer();
        customer.setId(customerId);

        List<Loan> loans = List.of(
                new Loan(1L, 5000.0, 12, LocalDate.now(), false, customer),
                new Loan(2L, 3000.0, 6, LocalDate.now(), true, customer),
                new Loan(3L, 7000.0, 12, LocalDate.now(), true, customer)
        );

        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        Mockito.when(loanRepository.findByCustomerId(customerId)).thenReturn(loans);

        List<LoanResponseDTO> response = loanService.listLoans(request);

        Assertions.assertEquals(1, response.size());
        Assertions.assertEquals(loans.get(0).getId(), response.get(0).getId());
    }

    @Test
    void listInstallment_ThrowExceptionIfNotFound() {
        Long loanId = 1000L;
        Mockito.when(loanInstallmentRepository.findByLoanId(loanId)).thenReturn(Collections.emptyList());

        CreditModuleException exception = Assertions.assertThrows(
                CreditModuleException.class,
                () -> loanService.listInstallments(loanId)
        );

        Assertions.assertEquals(ErrorMessage.INSTALLMENT_NOT_FOUND.getMessage(), exception.getErrorMessage());
    }

    @Test
    void listInstallments_ReturnsInstallmentsSuccessfully() {
        Long loanId = 100L;
        Loan loan = new Loan();
        loan.setId(loanId);
        List<LoanInstallment> installments = Arrays.asList(
                new LoanInstallment(1L, 100.0, 0.0, LocalDate.of(2024,12,17), LocalDate.of(2024,12,15), true,loan),
                new LoanInstallment(2L, 100.0, 100.0, LocalDate.of(2025,1,17), null, false,loan)
        );

        Mockito.when(loanInstallmentRepository.findByLoanId(loanId)).thenReturn(installments);

        List<LoanInstallmentResponseDTO> response = loanService.listInstallments(loanId);

        Assertions.assertEquals(2, response.size());

        LoanInstallmentResponseDTO firstInstallment = response.get(0);
        Assertions.assertEquals(1L, firstInstallment.getId());
        Assertions.assertEquals(100.0, firstInstallment.getAmount());
        Assertions.assertEquals(0.0, firstInstallment.getPaidAmount());
        Assertions.assertEquals(LocalDate.of(2024,12,17), firstInstallment.getDueDate());
        Assertions.assertEquals(LocalDate.of(2024,12,15), firstInstallment.getPaymentDate());
        Assertions.assertTrue(firstInstallment.getIsPaid());

        LoanInstallmentResponseDTO secondInstallment = response.get(1);
        Assertions.assertEquals(2L, secondInstallment.getId());
        Assertions.assertEquals(100.0, secondInstallment.getAmount());
        Assertions.assertEquals(100.0, secondInstallment.getPaidAmount());
        Assertions.assertEquals(LocalDate.of(2025,1,17), secondInstallment.getDueDate());
        Assertions.assertNull(secondInstallment.getPaymentDate());
        Assertions.assertFalse(secondInstallment.getIsPaid());
    }

    @Test
    void payLoan_loanNotFound() {
            Long loanId = 1L;
        double paymentAmount = 20.0;
        PayLoanRequest request = new PayLoanRequest(loanId, paymentAmount,LocalDate.now());

        Mockito.when(loanRepository.findById(loanId)).thenReturn(Optional.empty());

        CreditModuleException exception = Assertions.assertThrows(
                CreditModuleException.class,
                () -> loanService.payLoan(request)
        );

        Assertions.assertEquals(ErrorMessage.LOAN_NOT_FOUND.getMessage(), exception.getErrorMessage());
    }

    @Test
    void payLoan_noPayableInstallments() {
        Long loanId = 1L;
        double paymentAmount = 20.0;
        PayLoanRequest request = new PayLoanRequest(loanId, paymentAmount,LocalDate.now());

        Loan loan = new Loan();
        loan.setId(loanId);
        Mockito.when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));

        List<LoanInstallment> payableInstallments = List.of();
        Mockito.when(loanInstallmentRepository.findByLoanId(loanId)).thenReturn(payableInstallments);

        CreditModuleException exception = Assertions.assertThrows(
                CreditModuleException.class,
                () -> loanService.payLoan(request)
        );

        Assertions.assertEquals(ErrorMessage.NO_PAYABLE_INSTALLMENTS.getMessage(), exception.getErrorMessage());
    }
    @Test
    void payLoan_successPayPayableInstallments() {
        Long loanId = 1L;
        double paymentAmount = 1500.0;
        PayLoanRequest request = new PayLoanRequest(loanId, paymentAmount,LocalDate.now());

        Customer customer = new Customer();
        customer.setUsedCreditLimit(1000.0);

        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setLoanAmount(3000.0);
        loan.setNumberOfInstallment(3);
        loan.setIsPaid(false);
        loan.setCustomer(customer);

        LoanInstallment installment1 = new LoanInstallment();
        installment1.setId(1L);
        installment1.setAmount(1000.0);
        installment1.setIsPaid(true);
        installment1.setPaidAmount(1000.0);
        installment1.setDueDate(LocalDate.of(2024, 1, 1));

        LoanInstallment installment2 = new LoanInstallment();
        installment2.setId(2L);
        installment2.setAmount(1000.0);
        installment2.setIsPaid(false);
        installment2.setPaidAmount(0.0);
        installment2.setDueDate(LocalDate.of(2024, 2, 1));

        LoanInstallment installment3 = new LoanInstallment();
        installment3.setId(3L);
        installment3.setAmount(1000.0);
        installment3.setIsPaid(false);
        installment3.setPaidAmount(0.0);
        installment3.setDueDate(LocalDate.of(2024, 3, 1));

        Mockito.when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        Mockito.when(loanInstallmentRepository.findByLoanId(loanId))
                .thenReturn(List.of(installment1, installment2, installment3));

        LoanPaymentResponseDTO response = loanService.payLoan(request);

        Assertions.assertEquals(1, response.getPaidInstallments());  // 2. taksit ödenmiş
        Assertions.assertEquals(500.0, response.getRemainingAmount());  // Geriye 500 TL
        Assertions.assertEquals(1, response.getUnpaidInstallments());  // ödenmeyen 1 taksit
        Assertions.assertEquals(true, installment2.getIsPaid());  // 2. taksit ödenmdi
        Assertions.assertEquals(false, installment3.getIsPaid());  // 3. taksit ödenmemiş
        Assertions.assertNotNull(installment2.getPaymentDate());  // Ödeme tarihi eklenmiş
        Assertions.assertNull(installment3.getPaymentDate());  // 3. taksit ödenmediği için ödeme tarihi null olmalı


        Mockito.verify(loanInstallmentRepository).save(installment2);
        Mockito.verify(loanInstallmentRepository, Mockito.never()).save(installment3);
    }

    @Test
    void payLoan_allInstallmenstPaid() {
        Long loanId = 1L;
        double paymentAmount = 1000.0;
        PayLoanRequest request = new PayLoanRequest(loanId, paymentAmount,LocalDate.now());

        Customer customer = new Customer();
        customer.setUsedCreditLimit(1000.0);

        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setLoanAmount(1000.0);
        loan.setNumberOfInstallment(2);
        loan.setIsPaid(false);
        loan.setCustomer(customer);

        LoanInstallment installment1 = new LoanInstallment();
        installment1.setId(2L);
        installment1.setAmount(500.0);
        installment1.setIsPaid(false);
        installment1.setPaidAmount(0.0);
        installment1.setDueDate(LocalDate.of(2024, 2, 1));

        LoanInstallment installment2 = new LoanInstallment();
        installment2.setId(3L);
        installment2.setAmount(500.0);
        installment2.setIsPaid(false);
        installment2.setPaidAmount(0.0);
        installment2.setDueDate(LocalDate.of(2024, 3, 1));

        Mockito.when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        Mockito.when(loanInstallmentRepository.findByLoanId(loanId))
                .thenReturn(List.of(installment1, installment2));

        LoanPaymentResponseDTO response = loanService.payLoan(request);

        Assertions.assertEquals(2, response.getPaidInstallments());
        Assertions.assertEquals(0.0, response.getRemainingAmount());
        Assertions.assertEquals(0, response.getUnpaidInstallments());
        Assertions.assertEquals(true, installment1.getIsPaid());
        Assertions.assertEquals(true, installment2.getIsPaid());
        Assertions.assertNotNull(installment2.getPaymentDate());
        Assertions.assertNotNull(installment1.getPaymentDate());

        Mockito.verify(loanRepository).save(loan);
        Mockito.verify(customerRepository).save(customer);


        Mockito.verify(loanInstallmentRepository).save(installment1);
        Mockito.verify(loanInstallmentRepository).save(installment2);

    }

    }



