package com.example.creditmodule.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCustomerRequestdDto {
    private String name;
    private String surname;
    private Double creditLimit;
    private Double usedCreditLimit;
}
