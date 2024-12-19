package com.example.creditmodule.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;



@Data
@AllArgsConstructor
public class PayLoanRequest {
    @NotNull(message = "Loan ID cannot be null.")
    private Long loanId;

    @NotNull(message = "Payment amount cannot be null.")
    @Positive(message = "Payment amount must be greater than 0.")
    private Double paymentAmount;

}
