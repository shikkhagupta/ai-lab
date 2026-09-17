package com.ailearning.aiengineering.mcpbasics.tools;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class CalculatorTools {

    @McpTool(name = "add", description = "This tool will add two numbers and return the result.")
    public int add(
            @McpToolParam(description = "The first number", required = true) int a,
            @McpToolParam(description = "The second number", required = true) int b) {
        return a + b;
    }
}