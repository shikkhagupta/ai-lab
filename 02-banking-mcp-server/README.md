# Banking MCP Server

A small Spring Boot application that exposes banking-domain functionality as an **MCP (Model Context Protocol) server**.

This project is part of the `ai-lab` learning repository and focuses on understanding how an MCP server connects an MCP tool to a real backend service and database.

## What this project demonstrates

The project exposes a customer-profile operation as an MCP tool:

```text
getCustomerProfile(userId)
```

The tool retrieves customer information from PostgreSQL through a normal Spring service and JPA repository.

The complete flow is:

```text
MCP request
    ↓
@McpTool
    ↓
CustomerProfileTools
    ↓
BankingService
    ↓
CustomerProfileRepository
    ↓
JPA / Hibernate
    ↓
PostgreSQL
```

## Technology

* Java 21
* Spring Boot 4.1.1
* Spring AI 2.0.1
* Spring Data JPA
* Hibernate
* PostgreSQL
* Maven
* MCP Streamable HTTP

## Project structure

```text
02-banking-mcp-server/
├── curl/
│   ├── initialize.sh
│   ├── tools-list.sh
│   └── get-customer-profile.sh
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/ailearning/aiengineering/bankingmcp/
│   │   │       └── customer/
│   │   │           ├── CustomerProfile.java
│   │   │           ├── CustomerProfileRepository.java
│   │   │           ├── CustomerProfileTools.java
│   │   │           ├── BankingService.java
│   │   │           └── CustomerNotFoundException.java
│   │   └── resources/
│   │       └── application.yaml
│   └── test/
│       └── java/
└── pom.xml
```

## Database

The application uses PostgreSQL running locally.

Database configuration:

```text
Host:     localhost
Port:     5432
Database: banking
User:     bankuser
Password: bankpass
```

The main table is:

```sql
CREATE TABLE customer_profiles (
    id UUID PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);
```

Example data:

```text
user-1001 → Alice Johnson → alice@example.com
user-1002 → Bob Smith     → bob@example.com
```

## Running the server

Start PostgreSQL first, then start the Spring Boot application:

```bash
mvn spring-boot:run
```

The server runs on:

```text
http://localhost:8081
```

The MCP endpoint is:

```text
http://localhost:8081/mcp
```

## MCP tool

The MCP tool is defined using Spring AI's `@McpTool` annotation:

```java
@McpTool(description = "Get a customer's profile using their user ID")
public CustomerProfile getCustomerProfile(String userId) {
    return bankingService.getCustomerByUserId(userId);
}
```

Spring AI discovers the annotated method and exposes it through MCP.

The resulting MCP tool has:

```text
Name:
getCustomerProfile

Input:
userId: string
```

## Testing with curl

For learning purposes, curl is used as a manual MCP protocol client.

This is useful for seeing the actual MCP messages before building a real MCP client application.

### 1. Initialize an MCP session

```bash
curl -i \
  -X POST http://localhost:8081/mcp \
  -H "Content-Type: application/json" \
  -H "Accept: application/json, text/event-stream" \
  -d '{
    "jsonrpc": "2.0",
    "id": 1,
    "method": "initialize",
    "params": {
      "protocolVersion": "2025-11-25",
      "capabilities": {},
      "clientInfo": {
        "name": "curl",
        "version": "1.0"
      }
    }
  }'
```

The response contains an MCP session ID.

Use that session ID in the following requests.

### 2. List available tools

Replace `YOUR_SESSION_ID` with the session ID returned from initialization.

```bash
curl -i \
  -X POST http://localhost:8081/mcp \
  -H "Content-Type: application/json" \
  -H "Accept: application/json, text/event-stream" \
  -H "Mcp-Session-Id: YOUR_SESSION_ID" \
  -d '{
    "jsonrpc": "2.0",
    "id": 2,
    "method": "tools/list"
  }'
```

The response should contain:

```text
getCustomerProfile
```

and an input schema containing:

```text
userId: string
```

### 3. Call the customer-profile tool

```bash
curl -i \
  -X POST http://localhost:8081/mcp \
  -H "Content-Type: application/json" \
  -H "Accept: application/json, text/event-stream" \
  -H "Mcp-Session-Id: YOUR_SESSION_ID" \
  -d '{
    "jsonrpc": "2.0",
    "id": 3,
    "method": "tools/call",
    "params": {
      "name": "getCustomerProfile",
      "arguments": {
        "userId": "user-1001"
      }
    }
  }'
```

Expected customer:

```json
{
  "email": "alice@example.com",
  "fullName": "Alice Johnson",
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "user-1001"
}
```

## What I learned

### 1. An MCP tool is not the business logic

The MCP tool acts as an entry point into the existing application:

```text
MCP
 ↓
CustomerProfileTools
 ↓
BankingService
```

The business logic remains in the service layer.

### 2. `@McpTool` exposes a Java method through MCP

The annotation tells Spring AI that the method should be available as an MCP tool.

Spring AI also generates the MCP input schema from the method signature.

For example:

```java
String userId
```

becomes a required string property in the MCP tool schema.

### 3. MCP uses JSON-RPC messages

The requests use methods such as:

```text
initialize
tools/list
tools/call
```

The MCP session is initialized first, and subsequent requests use the session ID.

### 4. curl is not the real application client

In this project curl is being used to manually test the MCP protocol.

A real MCP client will be built in the next project.

## Key learning

The important architectural idea is that MCP does not replace the application's business layer.

Instead, MCP provides a standardized interface through which an MCP client can discover and invoke capabilities exposed by the application.

```text
             MCP
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
```