package com.example.creditmodule.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import lombok.*;

@Data
public class CreateLoanRequestDTO {
    @NotNull(message = "Loan amount cannot be null.")
    @Min(value = 0, message = "Loan amount must be greater than 0.")
    private Double loanAmount;
    @NotNull(message = "Number of installments cannot be null.")
    private Integer numberOfInstallment;
    @NotNull(message = "Interest rate cannot be null.")
    @DecimalMin(value = "0.1", message = "Interest rate must be at least 0.1.")
    @DecimalMax(value = "0.5", message = "Interest rate must be at most 0.5.")
    private Double interestRate;
    @NotNull(message = "Customer ID cannot be null.")
    private Long customerId;
}
