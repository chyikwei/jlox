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

  @Test
  void testClassInstance() {
    String input = """
    class TestClass {}
    var t = TestClass();
    print t;
    """;
    String output = PrintOutputHelper.printOutput(input);
    assertEquals("TestClass instance", output);
  }

  @Test
  void testSetGetField() {
    String input = """
    class TestClass {}
    var t = TestClass();
    t.a = "123";
    print t.a;
    """;
    String output = PrintOutputHelper.printOutput(input);
    assertEquals("123", output);
  }
}
