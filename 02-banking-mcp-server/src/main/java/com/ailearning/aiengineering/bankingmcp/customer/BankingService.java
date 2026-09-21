package com.ailearning.aiengineering.bankingmcp.customer;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BankingService {

    private final CustomerProfileRepository customerProfileRepository;

    public BankingService(CustomerProfileRepository customerProfileRepository) {
        this.customerProfileRepository = customerProfileRepository;
    }

    public CustomerProfile getCustomerById(UUID id) {
        return customerProfileRepository.findById(id)
                .orElseThrow(() ->
                        new CustomerNotFoundException("Customer not found: " + id));
    }

    public CustomerProfile getCustomerByUserId(String userId) {
        return customerProfileRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new CustomerNotFoundException("Customer not found: " + userId));
    }

    public CustomerProfile getCustomerByName(String fullName) {
        return customerProfileRepository.findByFullNameIgnoreCase(fullName)
                .orElseThrow(() ->
                        new CustomerNotFoundException("Customer not found: " + fullName));
    }
}