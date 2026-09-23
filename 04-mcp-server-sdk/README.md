# 04 - MCP Server SDK

A standalone MCP server built with the **MCP Java SDK**, without Spring Boot or Spring AI.

This project implements an MCP server using **Streamable HTTP** and Jetty.

The purpose of this project is to understand what happens underneath the Spring AI abstractions used in the earlier MCP projects.

## What this project demonstrates

* Building an MCP server using the MCP Java SDK
* Using Streamable HTTP transport
* Running an HTTP server with Jetty
* Registering an MCP tool programmatically
* Defining a tool's input schema
* Handling `tools/list`
* Handling `tools/call`
* Returning an MCP `CallToolResult`
* Understanding the separation between:

  * HTTP server
  * MCP transport
  * MCP server
  * MCP tools

## Technology

* Java 21
* Maven
* MCP Java SDK 2.0.1
* Jetty 12
* Streamable HTTP

There is intentionally no:

* Spring Boot
* Spring AI

## Architecture

```text
                    HTTP
                     │
                     ▼
              ┌─────────────┐
              │    Jetty    │
              │  HTTP Server│
              └──────┬──────┘
                     │
                     ▼
              ┌─────────────┐
              │ MCP Servlet │
              │  Transport  │
              └──────┬──────┘
                     │
                     ▼
              ┌─────────────┐
              │ MCP Server  │
              │  SDK        │
              └──────┬──────┘
                     │
                     ▼
              ┌─────────────┐
              │ AdditionTool│
              │    add      │
              └─────────────┘
```

## Running the server

Run:

```text
McpServerApplication
```

from your IDE.

The server starts on:

```text
http://localhost:8082/mcp
```

Expected output:

```text
MCP server started on http://localhost:8082/mcp
```

## MCP Tool

The server exposes one tool:

```text
add
```

Description:

```text
Adds two numbers
```

Input:

```json
{
  "a": 10,
  "b": 20
}
```

The tool returns:

```text
30.0
```

## Tool structure

The tool definition and execution are kept separate from the server startup code.

```text
AdditionTool
├── definition()
└── execute(request)
```

`definition()` describes the MCP tool:

```text
name
description
input schema
```

`execute()` contains the actual Java logic:

```text
request
   ↓
read a and b
   ↓
add numbers
   ↓
CallToolResult
```

## How the MCP flow works

When an MCP client connects, the MCP protocol handles the communication.

The client can discover available tools through:

```text
tools/list
```

The server responds with the registered tool and its input schema.

The client can then invoke the tool using:

```text
tools/call
```

For example:

```json
{
  "name": "add",
  "arguments": {
    "a": 10,
    "b": 20
  }
}
```

The MCP SDK routes the request to:

```text
AdditionTool.execute(...)
```

which returns the result.

## Why Jetty is needed

The MCP Java SDK provides the MCP server and Streamable HTTP transport, but the application still needs an HTTP server to accept network requests.

This project therefore uses Jetty:

```text
Jetty
  ↓
receives HTTP request

MCP Streamable HTTP transport
  ↓
handles MCP protocol transport

MCP Server SDK
  ↓
handles MCP operations

AdditionTool
  ↓
executes application logic
```

This is an important difference from Spring Boot.

In the Spring AI projects, Spring Boot automatically provides much of this infrastructure.

Here, we explicitly wire the pieces together.

## Comparison with Project 02

### Project 02

```text
Spring Boot
    ↓
Spring AI
    ↓
MCP Java SDK
    ↓
Streamable HTTP
    ↓
MCP Client
```

Spring handles much of the application infrastructure and Spring AI provides convenient MCP abstractions.

### Project 04

```text
Java application
    ↓
Jetty
    ↓
MCP Java SDK
    ↓
Streamable HTTP
    ↓
MCP Client
```

This project intentionally removes Spring so that the underlying MCP SDK becomes visible.

## Learning goal

The goal is not to build a production-ready server yet.

The goal is to understand the layers underneath Spring AI:

```text
Application
    ↓
HTTP server
    ↓
MCP transport
    ↓
MCP protocol
    ↓
MCP tool
```