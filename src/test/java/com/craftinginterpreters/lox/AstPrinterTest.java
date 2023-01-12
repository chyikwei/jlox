package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AstPrinterTest {

  @Test
  void testPrintLiteral() {
    Expr expr = new Expr.Literal(123);
    assertEquals("123", new AstPrinter().print(expr));
  }

  @Test
  void testPrintGrouping() {
    Expr expr = new Expr.Grouping(new Expr.Literal(123.45));
    assertEquals("(group 123.45)", new AstPrinter().print(expr));
  }

  @Test
  void testPrintUnary() {
    Expr expr = new Expr.Unary(new Token(TokenType.MINUS, "-", null, 1), new Expr.Literal(123));
    assertEquals("(- 123)", new AstPrinter().print(expr));
  }

  @Test
  void testPrintBinary() {
    Expr expr =
        new Expr.Binary(
            new Expr.Literal(123), new Token(TokenType.STAR, "*", null, 1), new Expr.Literal(456));
    assertEquals("(* 123 456)", new AstPrinter().print(expr));
  }

  @Test
  void testLogical() {
    Expr expr =
        new Expr.Logical(
            new Expr.Literal(123), new Token(TokenType.OR, "or", null, 1), new Expr.Literal(456));
    assertEquals("(or 123 456)", new AstPrinter().print(expr));
  }
}
