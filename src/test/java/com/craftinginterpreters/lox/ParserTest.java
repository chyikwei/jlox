package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParserTest
{
    @Test
    void testParseGroup() {
        List<Token> tokens = new Scanner("(\"123\" * 456);").scanTokens();
        Expr expr = new Parser(tokens).parseExpression();
        assertEquals("(group (* 123 456.0))", new AstPrinter().print(expr));
    }

    @Test
    void testParseEqual() {
        List<Token> tokens = new Scanner("123 == 456;").scanTokens();
        Expr expr = new Parser(tokens).parseExpression();
        assertEquals("(== 123.0 456.0)", new AstPrinter().print(expr));
    }
}
