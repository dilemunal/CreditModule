package com.example.creditmodule.service.impl;

import com.example.creditmodule.dto.request.CreateLoanRequestDTO;
import com.example.creditmodule.entity.Customer;
import com.example.creditmodule.entity.Loan;
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
    }

