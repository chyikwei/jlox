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

  @Test
  void testSimpleReturnValue() {
    String input = """
    fun add(a, b) {
      return a + b;
    }
    print add(1, 2);
    """;
    String output = PrintOutputHelper.printOutput(input);
    assertEquals("3", output);
  }

  @Test
  void testRecursiveReturnValue() {
    String input = """
    fun fib(n) {
      if (n <= 1) return n;
      return fib(n - 2) + fib(n - 1);
    }
    print fib(5);
    """;
    String output = PrintOutputHelper.printOutput(input);
    assertEquals("5", output);
  }

  @Test
  void testClosure() {
    String input = """
    fun makeCounter() {
      var i = 0;
      fun count() {
        i = i + 1;
        print i;
      }
      return count;
    }

    var counter = makeCounter();
    counter();
    counter();
    """;
    String output = PrintOutputHelper.printOutput(input);
    assertEquals("1\n2", output);
  }
  @Test
  void testVarBinding() {
    String input = """
    var a = "global";
    {
      fun showA() {
        print a;
      }
      showA();
      var a = "block";
      showA();
    }
    """;
    String output = PrintOutputHelper.printOutput(input);
    assertEquals("global\nglobal", output);
  }
}
