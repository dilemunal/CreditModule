package com.example.creditmodule.service.impl;

import com.example.creditmodule.dto.request.CreateCustomerRequestDto;
import com.example.creditmodule.entity.Customer;
import com.example.creditmodule.repository.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    public void testCreateCustomer_withValidInput() {
        CreateCustomerRequestDto request = new CreateCustomerRequestDto();
        request.setName("TEST");
        request.setSurname("test");
        request.setUsedCreditLimit(500.0);
        request.setCreditLimit(1000.0);

        Mockito.when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            return invocation.<Customer>getArgument(0);
        });

        Customer createdCustomer = customerService.createCustomer(request);

        Assertions.assertNotNull(createdCustomer);
        Assertions.assertEquals("TEST", createdCustomer.getName());
        Assertions.assertEquals("test", createdCustomer.getSurname());
        Assertions.assertEquals(500.0, createdCustomer.getUsedCreditLimit());
        Assertions.assertEquals(1000.0, createdCustomer.getCreditLimit());

        Mockito.verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    public void testCreateCustomer_withNullCreditLimits() {
        CreateCustomerRequestDto request = new CreateCustomerRequestDto();
        request.setName("test");
        request.setSurname("test");
        request.setUsedCreditLimit(null);
        request.setCreditLimit(null);

        Mockito.when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            return invocation.<Customer>getArgument(0);
        });

        Customer createdCustomer = customerService.createCustomer(request);

        Assertions.assertNotNull(createdCustomer);
        Assertions.assertEquals("test", createdCustomer.getName());
        Assertions.assertEquals("test", createdCustomer.getSurname());
        Assertions.assertEquals(0.0, createdCustomer.getUsedCreditLimit());
        Assertions.assertEquals(0.0, createdCustomer.getCreditLimit());

        Mockito.verify(customerRepository, times(1)).save(any(Customer.class));
    }
}