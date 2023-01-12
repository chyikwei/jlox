package com.craftinginterpreters.lox;

import java.io.ByteArrayOutputStream;
import java.util.List;

class PrintOutputHelper
{

  public static String printOutput(String script) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Interpreter interpreter = new Interpreter(outputStream);

    List<Stmt> statements = new Parser(new Scanner(script).scanTokens()).parse();
    interpreter.interpret(statements);
    return outputStream.toString().strip();
  }

  private PrintOutputHelper() {
  }

}
