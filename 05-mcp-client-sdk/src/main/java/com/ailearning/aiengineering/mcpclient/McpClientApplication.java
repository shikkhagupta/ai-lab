package com.ailearning.aiengineering.mcpclient;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.Map;

public class McpClientApplication {

    public static void main(String[] args) {

        /*
         * ---------------------------------------------------------
         * CLIENT 1 - Project 04
         * ---------------------------------------------------------
         *
         * Project 04 is our plain Java MCP server.
         *
         * URL:
         * http://localhost:8082/mcp
         */
        McpSyncClient sdkServerClient = createClient(
                "http://localhost:8082"
        );

        /*
         * ---------------------------------------------------------
         * CLIENT 2 - Project 02
         * ---------------------------------------------------------
         *
         * Project 02 is our Spring AI MCP server.
         *
         * URL:
         * http://localhost:8081/mcp
         */
        McpSyncClient springbootServerClient = createClient(
                "http://localhost:8081"
        );

        try {

            /*
             * Initialize both MCP sessions.
             *
             * This performs the MCP initialization handshake.
             */
            initializeClient(
                    "SDK Server",
                    sdkServerClient
            );

            initializeClient(
                    "Springboot Server",
                    springbootServerClient
            );

            /*
             * Discover and call the tools exposed by each server.
             */
            discoverAndCallAddTool(sdkServerClient);

            discoverAndCallCustomerProfileTool(springbootServerClient);

        }
        finally {

            /*
             * Always close the MCP clients when finished.
             */
            sdkServerClient.close();

            springbootServerClient.close();
        }
    }

    /*
     * Creates a synchronous MCP client using Streamable HTTP.
     *
     * This is the SDK equivalent of the Streamable HTTP client
     * configuration we used in Project 03 with Spring AI.
     */
    private static McpSyncClient createClient(String serverUrl) {

        var transport = HttpClientStreamableHttpTransport
                .builder(serverUrl)
                .endpoint("/mcp")
                .build();

        return McpClient.sync(transport)
                .build();
    }

    /*
     * Performs the MCP initialization handshake.
     */
    private static void initializeClient(
            String serverName,
            McpSyncClient client) {

        System.out.println();
        System.out.println(">>> Initializing " + serverName);

        var result = client.initialize();

        System.out.println(
                ">>> Protocol version: "
                        + result.protocolVersion()
        );

        System.out.println(
                ">>> Server: "
                        + result.serverInfo()
        );

        System.out.println(
                ">>> Capabilities: "
                        + result.capabilities()
        );
    }

    /*
     * Discover the tools provided by Project 04
     * and call the "add" tool.
     */
    private static void discoverAndCallAddTool(
            McpSyncClient client) {

        System.out.println();
        System.out.println(">>> Discovering SDK server tools");

        var tools = client.listTools().tools();

        tools.forEach(tool ->
                System.out.println("    Tool: " + tool.name())
        );

        var request = McpSchema.CallToolRequest
                .builder("add")
                .arguments(Map.of(
                        "a", 10,
                        "b", 20
                ))
                .build();

        System.out.println();
        System.out.println(">>> Calling add(10, 20)");

        var result = client.callTool(request);

        System.out.println(
                ">>> Add result: " + result
        );
    }

    /*
     * Discover the tools provided by Project 02
     * and call the "getCustomerProfile" tool.
     */
    private static void discoverAndCallCustomerProfileTool(
            McpSyncClient client) {

        System.out.println();
        System.out.println(">>> Discovering banking server tools");

        var tools = client.listTools().tools();

        tools.forEach(tool ->
                System.out.println("    Tool: " + tool.name())
        );

        var request = McpSchema.CallToolRequest
                .builder("getCustomerProfile")
                .arguments(Map.of(
                        "userId", "user-1001"
                ))
                .build();

        System.out.println();
        System.out.println(
                ">>> Calling getCustomerProfile(user-1001)"
        );

        var result = client.callTool(request);

        System.out.println(
                ">>> Customer result: " + result
        );
    }
}