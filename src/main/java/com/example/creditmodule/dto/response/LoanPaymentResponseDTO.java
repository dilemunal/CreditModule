package com.example.creditmodule.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class LoanPaymentResponseDTO {
    private Long loanId;
    private Double loanAmount;
    private Integer totalInstallments;
    private Integer paidInstallments;
    private Long unpaidInstallments;
    private Double remainingAmount; //extra amount
    private LocalDate paymentDate;
}
