package com.ailearning.aiengineering.mcpserver;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;
import java.util.Map;

/**
 * Defines the "add" MCP tool.
 *
 * The tool has two responsibilities:
 *
 * 1. Describe the tool to the MCP client:
 *    - tool name
 *    - description
 *    - input schema
 *
 * 2. Execute the tool when the MCP client calls it.
 */
public class AdditionTool {

    /**
     * Creates the MCP tool specification.
     *
     * The specification contains:
     * - the tool definition exposed through tools/list
     * - the method that handles tools/call
     */
    public McpServerFeatures.SyncToolSpecification specification() {

        // Describe what the MCP client can discover about this tool.
        var tool = McpSchema.Tool.builder(
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

        // Connect the tool definition with the method that executes it.
        //
        // When the MCP client sends tools/call for "add",
        // the MCP server invokes this::execute.
        return new McpServerFeatures.SyncToolSpecification(
                tool,
                this::execute
        );
    }

    /**
     * Executes the "add" MCP tool.
     *
     * The values come from the arguments sent by the MCP client.
     */
    public McpSchema.CallToolResult execute(
            McpSyncServerExchange exchange,
            McpSchema.CallToolRequest request) {

        // Read the arguments sent in the tools/call request.
        double a = ((Number) request.arguments().get("a")).doubleValue();
        double b = ((Number) request.arguments().get("b")).doubleValue();

        double result = a + b;

        // MCP tools return their result as CallToolResult.
        return McpSchema.CallToolResult.builder()
                .content(List.of(
                        McpSchema.TextContent
                                .builder(String.valueOf(result))
                                .build()
                ))
                .build();
    }
}