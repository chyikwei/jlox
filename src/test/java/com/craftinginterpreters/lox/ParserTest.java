package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ParserTest
{
    @Test
    void testParseGroup() {
        List<Token> tokens = new Scanner("(\"123\" * 456)").scanTokens();
        Expr expr = new Parser(tokens).parse();
        assertEquals("(group (* 123 456.0))", new AstPrinter().print(expr));
    }

    @Test
    void testParseEqual() {
        List<Token> tokens = new Scanner("123 == 456").scanTokens();
        Expr expr = new Parser(tokens).parse();
        assertEquals("(== 123.0 456.0)", new AstPrinter().print(expr));
    }
}