package com.ailearning.aiengineering.bankingmcp.customer;

import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class BankingService {

    private final CustomerProfileRepository customerProfileRepository;
    private final KYCDetailRepository kycDetailRepository;

    public BankingService(CustomerProfileRepository customerProfileRepository, KYCDetailRepository kycDetailRepository) {
        this.customerProfileRepository = customerProfileRepository;
        this.kycDetailRepository = kycDetailRepository;
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

    public KYCDetail getKYCDetailByUserId(String userId) {
        return kycDetailRepository.findByUserId(userId)
                .orElseThrow(() -> new CaseNotFoundException("No KYC case found for user: "));
    }

    public KYCDetail getLatestKYCDetailByUserId(String userId) {
        return kycDetailRepository.findFirstByUserIdAndStatusOrderBySubmittedAtDesc(userId, "PENDING")
                .orElseThrow(() -> new CaseNotFoundException("No pending KYC case found for user: "));
    }
}