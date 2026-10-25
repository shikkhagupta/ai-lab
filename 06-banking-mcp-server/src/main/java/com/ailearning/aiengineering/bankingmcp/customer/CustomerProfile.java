package com.ailearning.aiengineering.bankingmcp.customer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "customer_profiles")
public class CustomerProfile {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "cpr", nullable = false)
    private String cpr;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "address", nullable = false)
    private String address;

    protected CustomerProfile() {
        // Required by JPA
    }

    public UUID getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getCpr() {
        return cpr;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

}