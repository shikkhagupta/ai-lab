package com.ailearning.aiengineering.mcpclient;

import io.modelcontextprotocol.client.McpSyncClient;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.List;

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
    public void discoverTools3() {
        System.out.println(">>> Number of MCP clients: " + mcpSyncClients.size());

        for (int i = 0; i < mcpSyncClients.size(); i++) {
            var client = mcpSyncClients.get(i);

            System.out.println(">>> Client index: " + i);
            System.out.println(">>> Tools:");

            client.listTools()
                    .tools()
                    .forEach(tool ->
                            System.out.println("    " + tool.name())
                    );
        }
    }
}
