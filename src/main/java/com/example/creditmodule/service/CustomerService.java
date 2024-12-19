package com.example.creditmodule.service;

import com.example.creditmodule.dto.request.CreateCustomerRequestDto;
import com.example.creditmodule.entity.Customer;

public interface CustomerService {
    Customer createCustomer(CreateCustomerRequestDto createCustomerRequestDto);
}
