# MCP Client

A Spring Boot application for learning how to build an MCP client and communicate with an MCP server using Spring AI.

This project connects to the MCP server created in `02-mcp-tools-server`, discovers the tools exposed by that server, and invokes a tool through the MCP protocol.

## What this project demonstrates

This project focuses on the **MCP client side** of the communication.

It demonstrates:

* Connecting to an MCP server using Streamable HTTP
* MCP initialization and protocol negotiation
* Discovering tools exposed by an MCP server
* Calling an MCP tool
* Sending tool arguments
* Receiving and inspecting a tool result
* Understanding the interaction between an MCP client and MCP server

There is no LLM in this project. The client explicitly calls the MCP tool so that the MCP protocol itself can be understood first.

## Architecture

```text
┌──────────────────────────────┐
│       03-mcp-client          │
│                              │
│      Spring AI MCP Client    │
│              │               │
│              │ initialize    │
│              ▼               │
│        listTools()           │
│              │               │
│              │ tools/list    │
│              ▼               │
│        callTool()            │
│              │               │
│              │ tools/call    │
└──────────────┼───────────────┘
               │
               ▼
┌──────────────────────────────┐
│    02-mcp-tools-server       │
│                              │
│     @McpTool                 │
│          │                   │
│          ▼                   │
│     BankingService           │
│          │                   │
│          ▼                   │
│   CustomerProfileRepository  │
│          │                   │
│          ▼                   │
│       PostgreSQL             │
└──────────────────────────────┘
```

## MCP Server

This project connects to the MCP server from:

```text
02-mcp-tools-server
```

The server runs on:

```text
http://localhost:8081
```

The MCP endpoint is:

```text
/mcp
```

The client configuration uses the server base URL:

```yaml
spring:
  ai:
    mcp:
      client:
        streamable-http:
          connections:
            server:
              url: http://localhost:8081
```

The `/mcp` endpoint does not need to be included in the URL because the Spring AI MCP client uses `/mcp` as the default endpoint.

## Startup flow

When this application starts, Spring AI automatically creates and initializes the MCP client based on the configuration.

The flow is:

```text
Spring Boot starts
       │
       ▼
Spring AI MCP client auto-configuration
       │
       ▼
McpSyncClient created
       │
       ▼
MCP initialize request
       │
       ▼
02-mcp-tools-server
       │
       ▼
Server returns:
- protocol version
- capabilities
- server information
```

For example, the server reports:

```text
Protocol: 2025-11-25

Implementation:
    banking-mcp-server
    version: 0.0.1

Capabilities:
    tools
    resources
    prompts
    logging
```

## Tool discovery

After the MCP session is initialized, the client can discover the tools exposed by the server.

The client calls:

```java
var tools = client.listTools();
```

This corresponds to the MCP operation:

```text
tools/list
```

The server responds with the tools it makes available.

For this project:

```text
Tool: getCustomerProfile
```

The client does not need to know how this functionality is implemented.

It only knows the MCP tool contract.

## Calling a tool

The client creates a `CallToolRequest`:

```java
var request = McpSchema.CallToolRequest.builder("getCustomerProfile")
        .arguments(Map.of("userId", "user-1001"))
        .build();
```

Then invokes it:

```java
var result = client.callTool(request);
```

This corresponds to:

```text
tools/call
```

Conceptually, the request is:

```text
Tool:
    getCustomerProfile

Arguments:
    userId = user-1001
```

The MCP server receives the request and invokes its implementation.

## End-to-end flow

The complete request flow is:

```text
MCP Client
    │
    │ tools/call
    │
    │ getCustomerProfile
    │ userId = user-1001
    ▼
MCP Server
    │
    ▼
CustomerProfileTools
    │
    ▼
BankingService
    │
    ▼
CustomerProfileRepository
    │
    ▼
PostgreSQL
    │
    ▼
Customer profile
    │
    ▼
MCP Server
    │
    │ CallToolResult
    ▼
MCP Client
```

The result received by the client contains:

```json
{
  "email": "alice@example.com",
  "fullName": "Alice Johnson",
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "user-1001"
}
```

The result also indicates whether the MCP tool execution was successful:

```text
isError=false
```

## Important concept

The MCP client does **not** directly call:

```text
BankingService
CustomerProfileRepository
PostgreSQL
```

It only communicates with the MCP server through the MCP protocol.

The client knows about:

```text
getCustomerProfile
```

and its arguments.

The server owns the actual implementation.

This separation is one of the key ideas behind MCP.

## Running the project

### 1. Start the MCP server

First start:

```text
02-mcp-tools-server
```

Make sure it is running on:

```text
http://localhost:8081
```

### 2. Start the MCP client

From this project:

```bash
mvn spring-boot:run
```

The client connects to the MCP server during startup.

### 3. Expected output

You should see the MCP initialization information followed by tool discovery and invocation:

```text
>>> discoverTools() called
Tool: getCustomerProfile
>>> Tool result: CallToolResult[...]
```

The tool result should contain the customer profile for:

```text
user-1001
```

## Project structure

```text
03-mcp-client/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── ailearning/
    │   │           └── aiengineering/
    │   │               └── mcpclient/
    │   │                   ├── McpClientApplication.java
    │   │                   └── McpToolDiscovery.java
    │   └── resources/
    │       └── application.yaml
    └── test/
        └── java/
```

## Technology

* Java 21
* Spring Boot 4.1.1
* Spring AI 2.0.1
* MCP SDK 2.0.0
* Maven
* Streamable HTTP
* PostgreSQL (used by the MCP server)

## What this project does not cover

This project intentionally does not introduce:

* LLMs
* Spring AI ChatClient
* Automatic LLM tool calling
* RAG
* Vector databases
* LangChain
* AI agents

Those concepts can be introduced later.

The goal here is to understand MCP itself first:

```text
Connect
   ↓
Initialize
   ↓
Discover
   ↓
Call
   ↓
Receive result
```

## Learning progression

The MCP projects in this repository build on each other:

```text
01-mcp-basics
    │
    │ Understand MCP endpoint and protocol
    ▼
02-mcp-tools-server
    │
    │ Build an MCP server and expose tools
    ▼
03-mcp-client
    │
    │ Build an MCP client
    │ Discover and invoke tools
    ▼
Next AI project
    │
    │ Introduce AI/LLM interaction
    ▼
RAG / AI applications
```

The important foundation established by this project is:

> An MCP client can connect to an MCP server, discover the server's capabilities and tools, and invoke those tools through the MCP protocol without knowing how the server implements them.