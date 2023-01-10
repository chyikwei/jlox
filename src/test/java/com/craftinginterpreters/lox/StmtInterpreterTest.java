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
  private Map<String, String> printStatements = Map.ofEntries(
      entry("print 123;", "123"),
      entry("print 1 + 1;", "2"),
      entry("print (3 * 3);", "9")
  );

  @BeforeEach
  void init() {
    System.out.println("init");
    this.outputStream = new ByteArrayOutputStream();
    this.interpreter = new Interpreter(this.outputStream);
  }

  @Test
  void testPrintStmt() {
    for (Map.Entry<String, String> entry : printStatements.entrySet()) {
      List<Stmt> statements = new Parser(new Scanner(entry.getKey()).scanTokens()).parse();
      assertEquals(1, statements.size());
      this.interpreter.interpret(statements);
      String output = this.outputStream.toString().strip();
      assertEquals(entry.getValue(), output);
      this.outputStream.reset();
    }
  }
}
