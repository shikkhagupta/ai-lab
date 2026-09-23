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

        System.out.println(">>> Number of MCP clients: " + mcpSyncClients.size());

        for (McpSyncClient client : mcpSyncClients) {

            System.out.println(">>> Tools:");

            var tools = client.listTools().tools();

            tools.forEach(tool ->
                    System.out.println("    " + tool.name())
            );

            for (var tool : tools) {

                if (tool.name().equals("add")) {
                    callAddTool(client);
                }

                if (tool.name().equals("getCustomerProfile")) {
                    callCustomerProfileTool(client);
                }
            }
        }
    }

    private void callAddTool(McpSyncClient client) {

        System.out.println(">>> Calling add");

        var request = McpSchema.CallToolRequest.builder("add")
                .arguments(Map.of(
                        "a", 10,
                        "b", 20
                ))
                .build();

        var result = client.callTool(request);

        System.out.println(">>> Add result: " + result);
    }

    private void callCustomerProfileTool(McpSyncClient client) {

        System.out.println(">>> Calling getCustomerProfile");

        var request = McpSchema.CallToolRequest.builder("getCustomerProfile")
                .arguments(Map.of(
                        "userId", "user-1001"
                ))
                .build();

        var result = client.callTool(request);

        System.out.println(">>> Customer result: " + result);
    }
}