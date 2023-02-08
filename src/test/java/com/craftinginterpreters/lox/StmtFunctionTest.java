package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StmtFunctionTest
{
  @Test
  void testSimpleFunction() {
    String input = """
    fun add(a, b, c) {
      print a + b + c;
    }
    add(1, 2, 3);
    """;
    String output = PrintOutputHelper.printOutput(input);
    assertEquals("6", output);
  }

  @Test
  void testRecursiveFunction() {
    String input = """
    fun count(n) {
      if (n > 1) count(n - 1);
      print n;
    }
    count(3);
    """;
    String output = PrintOutputHelper.printOutput(input);
    assertEquals("1\n2\n3", output);
  }

    @Test
  void testFunctionName() {
    String input = """
    fun add(a, b) {
      print a + b;
    }
    print add;  
    """;
    String output = PrintOutputHelper.printOutput(input);
    assertEquals("<fn add>", output);
  }

}
