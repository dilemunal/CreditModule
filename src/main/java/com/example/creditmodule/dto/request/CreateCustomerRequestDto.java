package com.example.creditmodule.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCustomerRequestDto {
    @NotNull(message = "Name cannot be null.")
    private String name;
    @NotNull(message = "Surname cannot be null.")
    private String surname;
    private Double creditLimit;
    private Double usedCreditLimit;
}
