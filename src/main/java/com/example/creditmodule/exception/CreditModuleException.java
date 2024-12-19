package com.example.creditmodule.exception;

import com.example.creditmodule.enums.ErrorMessage;


public class CreditModuleException extends RuntimeException {

    private final ErrorMessage errorMessage;

    public CreditModuleException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }

    public Integer getErrorCode() {
        return errorMessage.getErrorCode();
    }

    public String getErrorMessage() {
        return errorMessage.getMessage();
    }
}
