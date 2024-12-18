package com.example.creditmodule.service.impl;

import com.example.creditmodule.dto.request.CreateCustomerRequestdDto;
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
    public Customer createCustomer(CreateCustomerRequestdDto createCustomerRequestdDto) {
        Customer customer = new Customer();
        customer.setName(createCustomerRequestdDto.getName());
        customer.setSurname(createCustomerRequestdDto.getSurname());
        customer.setUsedCreditLimit(createCustomerRequestdDto.getUsedCreditLimit() != null ? createCustomerRequestdDto.getUsedCreditLimit() : 0.0);
        customer.setCreditLimit(createCustomerRequestdDto.getCreditLimit() != null ? createCustomerRequestdDto.getCreditLimit() : 0.0);

        customerRepository.save(customer);

        return customer;
    }
}
