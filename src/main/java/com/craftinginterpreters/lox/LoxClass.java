package com.craftinginterpreters.lox;

import java.util.List;
import java.util.Map;

public class LoxClass implements LoxCallable
{
  final String name;
  final LoxClass superclass;
  private final Map<String, LoxFunction> methods;

  LoxClass(String name, LoxClass superclass, Map<String, LoxFunction> methods) {
    this.name = name;
    this.superclass = superclass;
    this.methods = methods;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public int arity()
  {
    return 0;
  }

  @Override
  public Object call(Interpreter interpreter, List<Object> arguments)
  {
    // TODO: pass arguments
    LoxInstance instance = new LoxInstance(this);
    return instance;
  }

  public LoxFunction findMethod(String name)
  {
    if (methods.containsKey(name)) {
      return methods.get(name);
    }

    if (superclass != null) {
      return superclass.findMethod(name);
    }

    return null;
  }
}
