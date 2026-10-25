package com.ailearning.aiengineering.bankingmcp.customer;

import java.time.LocalDateTime;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "kyc_cases")
public class KYCDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Column(name = "submitted_name", nullable = false)
    private String submittedName;

    @Column(name = "submitted_cpr", nullable = false)
    private String submittedCpr;

    @Column(name = "submitted_address", nullable = false)
    private String submittedAddress;

    @Column(name = "submitted_phone", nullable = false)
    private String submittedPhone;

    @Column(name = "submitted_email", nullable = false)
    private String submittedEmail;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;


    public KYCDetail() {
        // Required by JPA
    }   

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getSubmittedName() {
        return submittedName;
    }

    public String getSubmittedCpr() {
        return submittedCpr;
    }

    public String getSubmittedAddress() {
        return submittedAddress;
    }

    public String getSubmittedPhone() {
        return submittedPhone;
    }

    public String getSubmittedEmail() {
        return submittedEmail;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

}
