package com.ailearning.aiengineering.mcpserver;

import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.server.Server;

public class McpServerApplication {

    public static void main(String[] args) throws Exception {

        // Create a transport provider for the MCP server
        var transportProvider =
                HttpServletStreamableServerTransportProvider.builder()
                        .jsonMapper(McpJsonDefaults.getMapper())
                        .mcpEndpoint("/mcp")
                        .build();

        // Create the MCP server with the transport provider
        McpSyncServer mcpServer = McpServer.sync(transportProvider)
                .serverInfo("mcp-server-sdk", "0.0.1")
                .build();

        Server httpServer = new Server(8082);

        var context = new ServletContextHandler();
        context.setContextPath("/");

        // Whenever an HTTP request comes to /mcp, give it to the MCP Streamable HTTP transport.
        context.addServlet(
                transportProvider,
                "/mcp"
        );

        httpServer.setHandler(context);

        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> {
                    mcpServer.close();
                    try {
                        httpServer.stop();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                })
        );

        httpServer.start();

        System.out.println("MCP server started on http://localhost:8082/mcp");

        httpServer.join();
    }
}