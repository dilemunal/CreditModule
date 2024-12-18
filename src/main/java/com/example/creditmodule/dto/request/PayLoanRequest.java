package com.example.creditmodule.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PayLoanRequest {
    @NotNull(message = "Loan ID cannot be null.")
    private Long loanId;

    @NotNull(message = "Payment amount cannot be null.")
    @Positive(message = "Payment amount must be greater than 0.")
    private Double paymentAmount;

}
