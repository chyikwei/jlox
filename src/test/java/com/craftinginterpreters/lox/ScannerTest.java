package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScannerTest {
  @Test
  void testString() {
    Scanner scanner = new Scanner("\"apple\"");
    List<Token> tokens = scanner.scanTokens();
    assertEquals(2, tokens.size());
    Token token = tokens.get(0);
    assertEquals(new Token(TokenType.STRING, "\"apple\"", "apple", 1), token);
  }

  @Test
  void testNumber() {
    String[] validNumbers = {"123", "123.456"};
    for (String number : validNumbers) {
      Scanner scanner = new Scanner(number);
      List<Token> tokens = scanner.scanTokens();
      assertEquals(2, tokens.size());
      Token token = tokens.get(0);
      assertEquals(TokenType.NUMBER, token.type());
      assertEquals(number, token.lexeme());
    }
  }
}
