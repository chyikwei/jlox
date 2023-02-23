package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StmtClassTest
{
  @Test
  void testPrintClass() {
    String input = """
    class TestClass {
      test() {
        return "test";
      }
    }
    print TestClass;
    """;
    String output = PrintOutputHelper.printOutput(input);
    assertEquals("TestClass", output);
  }
}
