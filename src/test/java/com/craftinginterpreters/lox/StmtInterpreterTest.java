package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.*;

public class StmtInterpreterTest
{
  private Map<String, String> simplePrintStatements = Map.ofEntries(
      entry("print 123;", "123"),
      entry("print 1 + 1;", "2"),
      entry("print (3 * 3);", "9"),
      entry("print true;", "true"),
      entry("print \"one\";", "one")
  );

  private Map<String, String> printVarStatements = Map.ofEntries(
      entry("var a = 123; print a;", "123"),
      entry("var a = true; print a;", "true"),
      entry("var a = \"one\"; print a;", "one"),
      entry("var a = 1; var b = 1; print a + b;", "2")
      );

  private Map<String, String> printAssignStatements = Map.ofEntries(
      entry("var a = 123; a = 4; print a;", "4"),
      entry("var a = 1; var b = 1; a = b = 3; print a + b;", "6")
  );

  private Map<String, String> printIfElseStatements = Map.ofEntries(
      entry("if (1) print '123'; else print '456';", "123"),
      entry("if (false) print '123'; else print '456';", "456")
  );

  @Test
  void testSimplePrintStmt() {
    for (Map.Entry<String, String> entry : simplePrintStatements.entrySet()) {
      String output = PrintOutputHelper.printOutput(entry.getKey());
      assertEquals(entry.getValue(), output);
    }
  }

  @Test
  void testAssignPrintStmt() {
    for (Map.Entry<String, String> entry : printAssignStatements.entrySet()) {
      String output = PrintOutputHelper.printOutput(entry.getKey());
      assertEquals(entry.getValue(), output);
    }
  }

  @Test
  void testVarPrintStmt() {
    for (Map.Entry<String, String> entry : printVarStatements.entrySet()) {
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
  void testIfElse() {
      for (Map.Entry<String, String> entry : printIfElseStatements.entrySet()) {
        String output = PrintOutputHelper.printOutput(entry.getKey());
        assertEquals(entry.getValue(), output);
      }
  }
}
