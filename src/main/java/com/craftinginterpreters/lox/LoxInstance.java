package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.Map;

public class LoxInstance
{
  private LoxClass klass;
  private final Map<String, Object> fields;

  LoxInstance(LoxClass klass) {
    this.klass = klass;
    this.fields = new HashMap<>();
  }

  @Override
  public String toString() {
    return klass.name + " instance";
  }

  public Object get(Token name)
  {
    // fields first
    if (fields.containsKey(name.lexeme())) {
      return fields.get(name.lexeme());
    }

    // method 2nd
    LoxFunction method = klass.findMethod(name.lexeme());
    if (method != null) {
      return method.bind(this);
    }

    throw new RunTimeError(name, "Undefined property '" + name.lexeme() + "'.");
  }

  public void set(Token name, Object value)
  {
    fields.put(name.lexeme(), value);
  }
}
