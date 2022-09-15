package com.craftinginterpreters.lox;

import java.util.Objects;

public record Token(TokenType type, String lexeme, Object literal, int line) {

  public String toString() {
    return type + " " + lexeme + " " + literal;
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof Token other)) {
      return false;
    }
    return this.type == other.type && this.lexeme == other.lexeme
            && Objects.equals(this.literal, other.literal)
              && this.line == other.line;
  }
}
