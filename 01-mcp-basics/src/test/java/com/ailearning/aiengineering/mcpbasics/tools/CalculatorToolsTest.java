package com.ailearning.aiengineering.mcpbasics.tools;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculatorToolsTest {

    private final CalculatorTools calculatorTools = new CalculatorTools();

    @Test
    void addsTwoNumbers() {
        assertEquals(12, calculatorTools.add(7, 5));
    }

    @Test
    void calculatesTaxAtTheDemonstrationRate() {
        assertEquals(new BigDecimal("25.00"), calculatorTools.calculateTax(new BigDecimal("100.00")));
    }
}
