package com.ailearning.aiengineering.mcpserver;

import io.modelcontextprotocol.json.McpJsonDefaults;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.HttpServletStreamableServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;

import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.server.Server;

public class McpServerApplication {

    public static void main(String[] args) throws Exception {

        /*
         * MCP SERVER CHECKLIST
         *
         * 1. Transport
         *    How does the MCP client communicate with the server?
         *
         * 2. Server
         *    Create the MCP server using the transport.
         *
         * 3. Server capabilities
         *    Tell the MCP client which MCP features this server supports.
         *    For a tool server, tools(true) is required.
         *
         * 4. Tool specification
         *    Register the tools that the server exposes.
         *
         * 5. HTTP server
         *    Start the actual HTTP server that receives requests.
         */

        /*
         * 1. TRANSPORT
         *
         * Streamable HTTP is the MCP communication mechanism.
         *
         * The MCP endpoint will be:
         * http://localhost:8082/mcp
         */
        var transportProvider =
                HttpServletStreamableServerTransportProvider.builder()
                        .jsonMapper(McpJsonDefaults.getMapper())
                        .mcpEndpoint("/mcp")
                        .build();

        /*
         * Create the tool implementation.
         *
         * AdditionTool contains:
         * - the tool definition
         * - the input schema
         * - the code that executes the tool
         */
        var additionTool = new AdditionTool();

        /*
         * 2. MCP SERVER
         *
         * Start building the MCP server using the transport.
         */
        McpSyncServer mcpServer = McpServer.sync(transportProvider)

                /*
                 * Tell the client who this MCP server is.
                 *
                 * This information is returned during MCP initialization.
                 */
                .serverInfo("mcp-server-sdk", "0.0.1")

                /*
                 * 3. SERVER CAPABILITIES
                 *
                 * Advertise which MCP features this server supports.
                 *
                 * tools(true) means:
                 * "This server provides MCP tools."
                 *
                 * Without this capability, a client cannot use
                 * tools/list or tools/call for this server.
                 */
                .capabilities(
                        McpSchema.ServerCapabilities.builder()
                                .tools(true)
                                .build()
                )

                /*
                 * 4. TOOL SPECIFICATION
                 *
                 * Register the tool with the MCP server.
                 *
                 * additionTool.specification() contains:
                 * - tool name
                 * - description
                 * - input schema
                 * - handler method
                 */
                .tools(additionTool.specification())

                /*
                 * Finish building the MCP server.
                 */
                .build();

        /*
         * 5. HTTP SERVER
         *
         * Jetty is the actual HTTP server.
         *
         * MCP itself is the protocol.
         * Streamable HTTP is the MCP transport.
         * Jetty provides the HTTP server that carries that transport.
         */
        Server httpServer = new Server(8082);

        var context = new ServletContextHandler();
        context.setContextPath("/");

        /*
         * Route /mcp requests to the MCP Streamable HTTP transport.
         */
        context.addServlet(
                transportProvider,
                "/mcp"
        );

        httpServer.setHandler(context);

        /*
         * Graceful shutdown.
         *
         * When the Java application stops:
         * 1. Close the MCP server.
         * 2. Stop the HTTP server.
         */
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

        /*
         * Start Jetty and wait for requests.
         */
        httpServer.start();

        System.out.println(
                "MCP server started on http://localhost:8082/mcp"
        );

        httpServer.join();
    }
}