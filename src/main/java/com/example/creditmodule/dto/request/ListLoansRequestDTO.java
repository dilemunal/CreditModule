package com.example.creditmodule.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class ListLoansRequestDTO {
    @NotNull(message = "Customer ID cannot be null.")
    private Long customerId;
    private Integer numberOfInstallment;
    private Boolean isPaid;
    }