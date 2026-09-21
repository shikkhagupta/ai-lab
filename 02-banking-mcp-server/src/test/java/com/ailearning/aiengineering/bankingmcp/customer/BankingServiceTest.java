package com.ailearning.aiengineering.bankingmcp.customer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class BankingServiceTest {

    @Autowired
    private BankingService bankingService;

    @Test
    void shouldFindCustomerByUserId() {

        CustomerProfile customer =
                bankingService.getCustomerByUserId("user-1001");

        assertNotNull(customer);
        assertEquals("Alice Johnson", customer.getFullName());
        assertEquals("alice@example.com", customer.getEmail());
    }
}