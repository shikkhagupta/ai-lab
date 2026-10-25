package com.ailearning.aiengineering.bankingmcp.customer;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.stereotype.Component;

@Component
public class CustomerProfileTools {

    private final BankingService bankingService;

    public CustomerProfileTools(BankingService bankingService) {
        this.bankingService = bankingService;
        System.out.println(">>> CustomerProfileTools bean created");
    }

    @McpTool(description = "Get a customer's profile using their user ID")
    public CustomerProfile getCustomerProfile(String userId) {
        return bankingService.getCustomerByUserId(userId);
    }
}