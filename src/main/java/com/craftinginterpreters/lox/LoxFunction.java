package com.craftinginterpreters.lox;

import java.util.List;

public class LoxFunction implements LoxCallable
{
  private final Stmt.Function declaration;

  public LoxFunction(Stmt.Function declaration)
  {
    this.declaration = declaration;
  }

  @Override
  public int arity()
  {
    return declaration.params.size();
  }

  @Override
  public Object call(Interpreter interpreter, List<Object> arguments)
  {
    // TODO: fix later
    Environment environment = new Environment(interpreter.globals);
    for (int i = 0; i < declaration.params.size(); i++) {
      environment.define(declaration.params.get(i).lexeme(), arguments.get(i));
    }
    interpreter.evaluateBlock(declaration.body, environment);
    // TODO: fix later
    return null;
  }

  @Override
  public String toString() {
    return "<fn " + declaration.name.lexeme() + ">";
  }
}
