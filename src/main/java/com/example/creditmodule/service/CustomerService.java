package com.example.creditmodule.service;

import com.example.creditmodule.dto.request.CreateCustomerRequestdDto;
import com.example.creditmodule.entity.Customer;

public interface CustomerService {
    Customer createCustomer(CreateCustomerRequestdDto createCustomerRequestdDto);
}
