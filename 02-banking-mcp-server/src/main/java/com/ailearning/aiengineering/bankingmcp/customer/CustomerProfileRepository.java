package com.ailearning.aiengineering.bankingmcp.customer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, UUID> {

    Optional<CustomerProfile> findByUserId(String userId);

    Optional<CustomerProfile> findByFullNameIgnoreCase(String fullName);
}