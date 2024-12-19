package com.example.creditmodule.controller;

import com.example.creditmodule.dto.request.CreateCustomerRequestdDto;
import com.example.creditmodule.entity.Customer;
import com.example.creditmodule.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
public class CustomerController {

    @Autowired
    CustomerService customerService;

    @PostMapping("createCustomer")
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody CreateCustomerRequestdDto createCustomerRequestdDto) {
        try {
            Customer customer = customerService.createCustomer(createCustomerRequestdDto);
            return ResponseEntity.status(HttpStatus.OK).body(customer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
}
