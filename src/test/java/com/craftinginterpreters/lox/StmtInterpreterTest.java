package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.*;

public class StmtInterpreterTest
{
  private Map<String, String> simpleStatements = Map.ofEntries(
      entry("print 123;", "123"),
      entry("print 1 + 1;", "2"),
      entry("print (3 * 3);", "9"),
      entry("print true;", "true"),
      entry("print \"one\";", "one")
  );

  private Map<String, String> varStatements = Map.ofEntries(
      entry("var a = 123; print a;", "123"),
      entry("var a = true; print a;", "true"),
      entry("var a = \"one\"; print a;", "one"),
      entry("var a = 1; var b = 1; print a + b;", "2")
      );

  private Map<String, String> assignStatements = Map.ofEntries(
      entry("var a = 123; a = 4; print a;", "4"),
      entry("var a = 1; var b = 1; a = b = 3; print a + b;", "6")
  );

  private Map<String, String> ifElseStatements = Map.ofEntries(
      entry("if (1) print 123; else print 456;", "123"),
      entry("if (false) print 123; else print 456;", "456")
  );

  private Map<String, String> andOrStatements = Map.ofEntries(
      entry("var a = \"abc\" and 4; print a;", "4"),
      entry("print \"abc\" or 4;", "abc"),
      entry("print false and 4;", "false"),
      entry("print nil or 4;", "4")
  );

  @Test
  void testSimplePrintStmt() {
    for (Map.Entry<String, String> entry : simpleStatements.entrySet()) {
      String output = PrintOutputHelper.printOutput(entry.getKey());
      assertEquals(entry.getValue(), output);
    }
  }

  @Test
  void testAssignPrintStmt() {
    for (Map.Entry<String, String> entry : assignStatements.entrySet()) {
      String output = PrintOutputHelper.printOutput(entry.getKey());
      assertEquals(entry.getValue(), output);
    }
  }

  @Test
  void testVarPrintStmt() {
    for (Map.Entry<String, String> entry : varStatements.entrySet()) {
      String output = PrintOutputHelper.printOutput(entry.getKey());
      assertEquals(entry.getValue(), output);
    }
  }
  @Test
  void testBlock() {
    String input = """
        var a = "global_a";
        print a;
        {
          var a = "inner_a";
          print a;
        }
        print a;
        """;
    String output = PrintOutputHelper.printOutput(input);
    assertEquals("global_a\ninner_a\nglobal_a", output);
  }

  @Test
  void testIfElseStmt() {
      for (Map.Entry<String, String> entry : ifElseStatements.entrySet()) {
        String output = PrintOutputHelper.printOutput(entry.getKey());
        assertEquals(entry.getValue(), output);
      }
  }

  @Test
  void testAndOrStmt() {
    for (Map.Entry<String, String> entry : andOrStatements.entrySet()) {
      String output = PrintOutputHelper.printOutput(entry.getKey());
      assertEquals(entry.getValue(), output);
    }
  }

  @Test
  void testWhileLoop() {
    String input = """
        var a = 0;
        while (a < 3) {
          print a;
          a = a + 1;
        }
        """;
    String output = PrintOutputHelper.printOutput(input);
    assertEquals("0\n1\n2", output);
  }

  @Test
  void testForLoop() {
    String input = """
       for (var i = 0; i < 3; i = i + 1) {
         print i;  
       }
        """;
    String output = PrintOutputHelper.printOutput(input);
    assertEquals("0\n1\n2", output);
  }


}
