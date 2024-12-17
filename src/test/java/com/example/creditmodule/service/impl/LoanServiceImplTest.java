package com.example.creditmodule.service.impl;

import com.example.creditmodule.dto.request.CreateLoanRequestDTO;
import com.example.creditmodule.dto.request.ListLoansRequestDTO;
import com.example.creditmodule.dto.response.LoanInstallmentResponseDTO;
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
}

