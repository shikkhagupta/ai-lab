package com.ailearning.aiengineering.mcpbasics.tools;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class TimeToolsTest {

    private final TimeTools timeTools = new TimeTools();

    @Test
    void returnsAnIso8601UtcTimestamp() {
        assertDoesNotThrow(() -> Instant.parse(timeTools.getCurrentTime()));
    }
}
