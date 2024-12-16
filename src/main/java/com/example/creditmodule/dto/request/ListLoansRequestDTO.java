package com.example.creditmodule.dto.request;


import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ListLoansRequestDTO {
    @NotNull(message = "Customer ID cannot be null.")
    private Long customerId;
    private Integer numberOfInstallment;
    private Boolean isPaid;
    }