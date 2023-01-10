package com.craftinginterpreters.lox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.*;

public class StmtInterpreterTest
{
  private ByteArrayOutputStream outputStream;
  private Interpreter interpreter;
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


  @BeforeEach
  void init() {
    this.outputStream = new ByteArrayOutputStream();
    this.interpreter = new Interpreter(this.outputStream);
  }

  @Test
  void testSimplePrintStmt() {
    for (Map.Entry<String, String> entry : simplePrintStatements.entrySet()) {
      List<Stmt> statements = new Parser(new Scanner(entry.getKey()).scanTokens()).parse();
      assertEquals(1, statements.size());
      this.interpreter.interpret(statements);
      String output = this.outputStream.toString().strip();
      assertEquals(entry.getValue(), output);
      this.outputStream.reset();
    }
  }

  @Test
  void testVarPrintStmt() {
    for (Map.Entry<String, String> entry : printVarStatements.entrySet()) {
      List<Stmt> statements = new Parser(new Scanner(entry.getKey()).scanTokens()).parse();
      this.interpreter.interpret(statements);
      String output = this.outputStream.toString().strip();
      assertEquals(entry.getValue(), output);
      this.outputStream.reset();
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
    List<Stmt> statements = new Parser(new Scanner(input).scanTokens()).parse();
    this.interpreter.interpret(statements);
    String output = this.outputStream.toString().strip();
    assertEquals("global_a\ninner_a\nglobal_a", output);
  }
}
