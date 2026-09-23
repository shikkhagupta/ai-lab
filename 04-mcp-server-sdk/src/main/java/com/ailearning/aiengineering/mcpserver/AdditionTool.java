package com.ailearning.aiengineering.mcpserver;

import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;
import java.util.Map;

public class AdditionTool {

    public McpSchema.Tool definition() {
    return McpSchema.Tool.builder(
            "add",
            Map.of(
                    "type", "object",
                    "properties", Map.of(
                            "a", Map.of("type", "number"),
                            "b", Map.of("type", "number")
                    ),
                    "required", List.of("a", "b")
            )
    )
    .description("Adds two numbers")
    .build();
}

    public McpSchema.CallToolResult execute(
        McpSchema.CallToolRequest request) {

    double a = ((Number) request.arguments().get("a")).doubleValue();
    double b = ((Number) request.arguments().get("b")).doubleValue();

    double result = a + b;

    return McpSchema.CallToolResult.builder()
            .content(List.of(
                    McpSchema.TextContent.builder(String.valueOf(result)).build()
            ))
            .build();
}
}