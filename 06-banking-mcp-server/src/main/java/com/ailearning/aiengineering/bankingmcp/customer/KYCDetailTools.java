package com.ailearning.aiengineering.bankingmcp.customer;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.stereotype.Component;

@Component
public class KYCDetailTools {

    private final BankingService bankingService;

    public KYCDetailTools(BankingService bankingService) {
        this.bankingService = bankingService;
        System.out.println(">>> KYCDetailTools bean created");
    }

    @McpTool(description = "Get a customer's KYC details using their user ID")
    public KYCDetail getKYCDetail(String userId) {
        return bankingService.getKYCDetailByUserId(userId);
    }

    @McpTool(description = "Get a customer's latest KYC details using their user ID")
    public KYCDetail getLatestKYCDetail(String userId) {
        return bankingService.getLatestKYCDetailByUserId(userId);
    }
}
