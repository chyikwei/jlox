package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.Map;

public class Environment
{
  final Environment enclosing;
  private final Map<String, Object> values;

  Environment(Environment enclosing) {
    this.enclosing = enclosing;
    this.values = new HashMap<>();
  }
  Environment() {
    this(null);
  }

  Object get(Token name) {
    if (values.containsKey(name.lexeme())) {
      return values.get(name.lexeme());
    }
    if (enclosing != null) {
      return enclosing.get(name);
    }

    throw new RunTimeError(name, "Undefined variable '" + name.lexeme() + "'");
  }

  Object getAt(int distance, String name) {
    return ancestor(distance).values.get(name);
  }

  void assignAt(int distance, Token name, Object value) {
    ancestor(distance).values.put(name.lexeme(), value);
  }

  Environment ancestor(int distance) {
    Environment environment = this;
    for (int i = 0; i < distance; i++) {
      environment = environment.enclosing;
    }
    return environment;
  }

  void define(String name, Object value) {
    values.put(name, value);
  }

  void assign(Token name, Object value) {
    if (values.containsKey(name.lexeme())) {
      values.put(name.lexeme(), value);
      return;
    }

    if (enclosing != null) {
      // assign Var in outer scopes is valid
      enclosing.assign(name, value);
      return;
    }

    throw new RunTimeError(name, "Undefined variable '" + name.lexeme() + "'");
  }
}
