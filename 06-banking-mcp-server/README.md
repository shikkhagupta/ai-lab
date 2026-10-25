# Banking MCP Server

A small Spring Boot application that serves as the **mock core-banking backend** for [`bank-ops-agent`], and exposes banking-domain functionality as an **MCP (Model Context Protocol) server**.

This project is part of the `ai-lab` learning repository. It started as an exercise in connecting an MCP tool to a real backend service and database, and it now also holds the synthetic data that `bank-ops-agent` reads when it works a KYC case.

**All data is synthetic. No real customer data is used anywhere in this project.**

## Role in bank-ops-agent

`bank-ops-agent` is a back-office tool. It starts working after the customer's workflow (submitting a KYC form) has finished, and it proposes a decision for a human analyst to approve. This application plays the part of the bank's own system that the agent calls:

```text
analyst screen  →  bank-ops-agent  →  spring application (JAVA) →  PostgreSQL
```

The agent does not care how this application is built, only that its endpoints return what it expects.

The KYC review is periodic (first-time and re-verification). A case needs two sets of data to compare:

* **On file:** what the bank already holds about the customer (`customer_profiles`)
* **Submitted:** what the customer submitted in the review (`kyc_cases`)

The case is saved to the database first. The agent is then called with a case ID and fetches everything it needs from here. A human always approves the agent's proposal.

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

## Status

* [x] `getCustomerProfile(userId)` exposed as an MCP tool
* [x] Customer profile includes CPR, phone and address
* [x] `kyc_cases` table, `KYCDetail` entity, repository and service methods
* [ ] KYC case exposed through the API for the agent

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
06-banking-mcp-server/
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
│   │   │           ├── KYCDetail.java
│   │   │           ├── KYCDetailRepository.java
│   │   │           ├── BankingService.java
│   │   │           ├── CustomerNotFoundException.java
│   │   │           └── CaseNotFoundException.java
│   │   └── resources/
│   │       └── application.yaml
│   └── test/
│       └── java/
└── pom.xml
```

## Database

The application uses PostgreSQL running locally in Docker.

Database configuration (local development credentials only):

```text
Host:     localhost
Port:     5432
Database: banking
User:     bankuser
Password: bankpass
```

Hibernate is configured with `ddl-auto: none`. It never creates or changes tables, so every table below must exist in PostgreSQL before the application starts.

### customer_profiles: what the bank has on file

```sql
CREATE TABLE customer_profiles (
    id UUID PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

ALTER TABLE customer_profiles
    ADD COLUMN cpr     VARCHAR(11),
    ADD COLUMN phone   VARCHAR(20),
    ADD COLUMN address VARCHAR(255);
```

The four KYC credentials in the Danish market are CPR, address, email and phone. Phone numbers are stored in international format (`+45...`).

### kyc_cases: what the customer submitted

```sql
CREATE TABLE kyc_cases (
    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id           VARCHAR(100) NOT NULL REFERENCES customer_profiles(user_id),
    submitted_name    VARCHAR(100) NOT NULL,
    submitted_cpr     VARCHAR(11)  NOT NULL,
    submitted_address VARCHAR(255) NOT NULL,
    submitted_phone   VARCHAR(20)  NOT NULL,
    submitted_email   VARCHAR(255) NOT NULL,
    status            VARCHAR(20)  NOT NULL DEFAULT 'SUBMITTED',
    submitted_at      TIMESTAMP    NOT NULL DEFAULT now()
);
```

There is no customer-facing form in this project. Cases are inserted directly into this table to stand in for a form submission.

### Example data

```text
customer_profiles
user-1001 → John Smith → john@example.com   (CPR, phone and address filled in)
user-1002 → Bob Smith  → bob@example.com    (CPR, phone and address filled in)

kyc_cases
1 → user-1001 → submitted with nothing changed
2 → user-1002 → submitted with a different phone number
```

All CPR numbers, phone numbers and addresses are made up.

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

The response contains an MCP session ID in the `Mcp-Session-Id` header.

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

The response includes the customer's name, email, CPR, phone and address.

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

### 5. Spring Data builds queries from method names

A repository method name is turned into SQL, with no query written by hand:

```java
Optional<KYCDetail> findByUserId(String userId);
```

`findBy` + `UserId` becomes `WHERE user_id = ?`. The second type parameter in `JpaRepository<KYCDetail, Long>` is the type of the entity's `@Id` field.

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