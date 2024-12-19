package com.example.creditmodule.enums;

import lombok.Getter;

@Getter
public enum ErrorMessage {
    CUSTOMER_NOT_FOUND(1001, "Customer not found with given id."),
    INSUFFICIENT_CREDIT_LIMIT(1002, "Customer does not have enough credit limit for the loan."),
    INVALID_NUMBER_OF_INSTALLMENTS(1003, "Number of installments must be one of the following: 6, 9, 12, 24."),
    LOAN_NOT_FOUND(1004, "Loan not found for given user."),
    INSTALLMENT_NOT_FOUND(1005, "Installment not found for given loanId."),
    LOAN_ALREADY_PAID(1006, "Loan with given ID already paid."),
    INVALID_PAYMENT_AMOUNT(1007, "Payment amount cannot be less than loan amount."),
    NO_PAYABLE_INSTALLMENTS(1008, "There are no payable installments.");

    private final Integer errorCode;
    private final String message;

    ErrorMessage(Integer errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

}

