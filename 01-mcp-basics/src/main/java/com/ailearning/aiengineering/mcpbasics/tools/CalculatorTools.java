package com.ailearning.aiengineering.mcpbasics.tools;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class CalculatorTools {

    private static final BigDecimal DEMONSTRATION_TAX_RATE = new BigDecimal("0.25");

    @McpTool(name = "add", description = "This tool will add two numbers and return the result.")
    public int add(
            @McpToolParam(description = "The first number", required = true) int a,
            @McpToolParam(description = "The second number", required = true) int b) {
        return a + b;
    }

    @McpTool(name = "calculateTax", description = "Calculate tax at the demonstration rate of 25%")
    public BigDecimal calculateTax(
            @McpToolParam(description = "The amount before tax", required = true) BigDecimal amount) {
        return amount.multiply(DEMONSTRATION_TAX_RATE).setScale(2, RoundingMode.HALF_UP);
    }
}
