package com.ailearning.aiengineering.mcpbasics.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculatorToolsTest {

    private final CalculatorTools calculatorTools = new CalculatorTools();

    @Test
    void addsTwoNumbers() {
        assertEquals(12, calculatorTools.add(7, 5));
    }
}
