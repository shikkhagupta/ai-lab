package com.ailearning.aiengineering.mcpclient;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class McpToolDiscovery {

    private final List<McpSyncClient> mcpSyncClients;

    public McpToolDiscovery(List<McpSyncClient> mcpSyncClients) {
        this.mcpSyncClients = mcpSyncClients;
    }

    @PostConstruct
    public void discoverTools() {
        System.out.println(">>> discoverTools() called");

        mcpSyncClients.forEach(client -> {
            var result = client.listTools();

            result.tools().forEach(tool ->
                    System.out.println("Tool: " + tool.name())
            );
        });
    }

    @PostConstruct
    public void discoverTools2() {
        System.out.println(">>> discoverTools2() called");

        var client = mcpSyncClients.get(0);
        var tools = client.listTools();
        tools.tools().forEach(tool ->
                System.out.println("Tool: " + tool.name())
        );

        var request = McpSchema.CallToolRequest.builder("getCustomerProfile")
                .arguments(Map.of("userId", "user-1001"))
                .build();
        var result = client.callTool(request);

        System.out.println(">>> Tool result: " + result);

    }
}