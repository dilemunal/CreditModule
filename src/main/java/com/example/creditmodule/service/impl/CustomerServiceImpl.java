package com.example.creditmodule.service.impl;

import com.example.creditmodule.dto.request.CreateCustomerRequestDto;
import com.example.creditmodule.entity.Customer;
import com.example.creditmodule.repository.CustomerRepository;
import com.example.creditmodule.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Override
    public Customer createCustomer(CreateCustomerRequestDto createCustomerRequestDto) {
        Customer customer = new Customer();
        customer.setName(createCustomerRequestDto.getName());
        customer.setSurname(createCustomerRequestDto.getSurname());
        customer.setUsedCreditLimit(createCustomerRequestDto.getUsedCreditLimit() != null ? createCustomerRequestDto.getUsedCreditLimit() : 0.0);
        customer.setCreditLimit(createCustomerRequestDto.getCreditLimit() != null ? createCustomerRequestDto.getCreditLimit() : 0.0);

        customerRepository.save(customer);

        return customer;
    }
}
