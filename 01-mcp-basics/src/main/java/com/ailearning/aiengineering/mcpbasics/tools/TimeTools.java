package com.ailearning.aiengineering.mcpbasics.tools;

import java.time.Instant;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.stereotype.Component;

@Component
public class TimeTools {

    @McpTool(name = "getCurrentTime", description = "Get the server's current time in UTC")
    public String getCurrentTime() {
        return Instant.now().toString();
    }
}
