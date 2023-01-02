package com.craftinginterpreters.lox;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.*;

class InterpreterTest
{
    private Map<String, Double> numericExpressions = Map.ofEntries(
            entry("123", 123.0),
            entry("-123", -123.0),
            entry("1 + 1 + 1", 3.0),
            entry("2 - 1", 1.0),
            entry("1 / 2", 0.5)
    );

    private Map<String, Boolean> booleanExpressions = Map.ofEntries(
        entry("123 == 123", true),
        entry("123 >= 123", true),
        entry("1 != 2", true),
        entry("2 != 2", false),
        entry("3 > 4", false)
    );

    private Interpreter interpreter = new Interpreter();
    @Test
    void testNumericValues() {

        for (Map.Entry<String, Double> entry : numericExpressions.entrySet()) {
            Expr expr = new Parser(new Scanner(entry.getKey()).scanTokens()).parse();
            Object eval = interpreter.evaluate(expr);
            assertEquals(entry.getValue(), (double) eval, 1e-10);
        }
    }
    @Test
    void testBooleanValues() {

        for (Map.Entry<String, Boolean> entry : booleanExpressions.entrySet()) {
            Expr expr = new Parser(new Scanner(entry.getKey()).scanTokens()).parse();
            Object eval = interpreter.evaluate(expr);
            assertEquals(entry.getValue(), (boolean) eval);
        }
    }
}
