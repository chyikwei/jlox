package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
      List<Token> tokens = new Scanner(number).scanTokens();
      assertEquals(2, tokens.size());
      assertEquals(TokenType.NUMBER, tokens.get(0).type());
      assertEquals(number, tokens.get(0).lexeme());
    }
  }

  @Test
  void testIdentifier() {
    String[] identifiers = {"abc", "ABc", "AB_C", "A_and_B", "and_A"};
    for (String id : identifiers) {
      List<Token> tokens = new Scanner(id).scanTokens();
      assertEquals(2, tokens.size());
      assertEquals(TokenType.IDENTIFIER, tokens.get(0).type());
    }
  }

  @Test
  void testKeyword() {
    Map<String, TokenType> keywords =
        Stream.of(
                new Object[][] {
                  {"and", TokenType.AND},
                  {"return", TokenType.RETURN},
                })
            .collect(Collectors.toMap(data -> (String) data[0], data -> (TokenType) data[1]));

    for (Map.Entry<String, TokenType> entry : keywords.entrySet()) {
      List<Token> tokens = new Scanner(entry.getKey()).scanTokens();
      assertEquals(2, tokens.size());
      assertEquals(entry.getValue(), tokens.get(0).type());
    }
  }

  @Test
  void testExpression() {
    String source = "var a = 4.5;";
    TokenType[] tokenTypes = {
      TokenType.VAR,
      TokenType.IDENTIFIER,
      TokenType.EQUAL,
      TokenType.NUMBER,
      TokenType.SEMICOLON,
      TokenType.EOF
    };
    List<Token> tokens = new Scanner(source).scanTokens();
    assertEquals(tokenTypes.length, tokens.size());
    for (int i = 0; i < tokenTypes.length; i++) {
      assertEquals(tokenTypes[i], tokens.get(i).type());
    }
  }
}
