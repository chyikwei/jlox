package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void>
{
  private enum ClasType {
    NONE,
    CLASS,
    SUBCLASS
  }
  private final Interpreter interpreter;
  private final Stack<Map<String, Boolean>> scopes;
  private FunctionType currentFunction;

  private ClasType currentClass;


  Resolver(Interpreter interpreter) {
    this.interpreter = interpreter;
    this.scopes = new Stack<>();
    currentFunction = FunctionType.NONE;
    currentClass = ClasType.NONE;
  }

  void resolve(List<Stmt> statements) {
    for (Stmt statement : statements) {
      resolve(statement);
    }
  }

  private void resolve(Stmt stmt) {
    stmt.accept(this);
  }

  private void resolve(Expr expr) {
    expr.accept(this);
  }

  private void beginScope() {
    scopes.push(new HashMap<>());
  }

  private void endScope() {
    scopes.pop();
  }

  private void declare(Token name) {
    if (scopes.isEmpty()) {
      return;
    }
    Map<String, Boolean> scope = scopes.peek();
    if (scope.containsKey(name.lexeme())) {
      Lox.error(name, "Already have variable with this name in this scope.");
    }

    scope.put(name.lexeme(), false);
  }

  private void define(Token name) {
    if (scopes.isEmpty()) {
      return;
    }
    Map<String, Boolean> scope = scopes.peek();
    scope.put(name.lexeme(), true);
  }

  private void resolveLocal(Expr expr, Token name) {
    // resolve expr with closest var name in scope stacks
    for (int i = scopes.size() - 1; i >= 0; i--) {
      if (scopes.get(i).containsKey(name.lexeme())) {
        interpreter.resolve(expr, scopes.size() - 1 - i);
        return;
      }
    }
  }

  private void resolveFunction(Stmt.Function function, FunctionType type) {
    FunctionType enclosingFunction = currentFunction;
    currentFunction = type;

    beginScope();
    for (Token param : function.params) {
      declare(param);
      define(param);
    }
    resolve(function.body);
    endScope();

    currentFunction = enclosingFunction;
  }

  @Override
  public Void visitAssignExpr(Expr.Assign expr)
  {
    // resolve expr for assigned value first
    resolve(expr.value);
    resolveLocal(expr, expr.name);
    return null;
  }

  @Override
  public Void visitBinaryExpr(Expr.Binary expr)
  {
    resolve(expr.left);
    resolve(expr.right);
    return null;
  }

  @Override
  public Void visitCallExpr(Expr.Call expr)
  {
    resolve(expr.callee);

    for (Expr argument : expr.arguments) {
      resolve(argument);
    }
    return null;
  }

  @Override
  public Void visitGetExpr(Expr.Get expr)
  {
    resolve(expr.object);
    return null;
  }

  @Override
  public Void visitGroupingExpr(Expr.Grouping expr)
  {
    resolve(expr.expression);
    return null;
  }

  @Override
  public Void visitLiteralExpr(Expr.Literal expr)
  {
    // Do nothing
    return null;
  }

  @Override
  public Void visitLogicalExpr(Expr.Logical expr)
  {
    resolve(expr.left);
    resolve(expr.right);
    return null;
  }

  @Override
  public Void visitSetExpr(Expr.Set expr)
  {
    resolve(expr.value);
    resolve(expr.object);
    return null;
  }

  @Override
  public Void visitSuperExpr(Expr.Super expr)
  {
    if (currentClass == ClasType.NONE) {
      Lox.error(expr.keyword, "Can't use 'super' outside of a class.");
    } else if (currentClass != ClasType.CLASS) {
      Lox.error(expr.keyword, "Can't use 'super' in a class with no superclass.");
    }

    resolveLocal(expr, expr.keyword);
    return null;
  }

  @Override
  public Void visitThisExpr(Expr.This expr)
  {
    //TODO: add test case
    if (currentClass == ClasType.NONE) {
      Lox.error(expr.keyword, "Can't use 'this' outside of a class.");
      return null;
    }

    resolveLocal(expr, expr.keyword);
    return null;
  }

  @Override
  public Void visitUnaryExpr(Expr.Unary expr)
  {
    resolve(expr.right);
    return null;
  }

  @Override
  public Void visitVariableExpr(Expr.Variable expr)
  {
    // variable is declared but not defined
    if (!scopes.isEmpty() && scopes.peek().get(expr.name.lexeme()) == Boolean.FALSE) {
      Lox.error(expr.name, "Can't read local variable in its own initializer.");
    }
    resolveLocal(expr, expr.name);

    return null;
  }

  @Override
  public Void visitBlockStmt(Stmt.Block stmt)
  {
    beginScope();
    resolve(stmt.statements);
    endScope();
    return null;
  }

  @Override
  public Void visitClassStmt(Stmt.Class stmt)
  {
    // stack in function call
    ClasType enclosingClass = currentClass;
    currentClass = ClasType.CLASS;

    declare(stmt.name);
    define(stmt.name);

    // check self inherit
    if (stmt.superclass != null
        && stmt.name.lexeme().equals(stmt.superclass.name.lexeme())
    ) {
      Lox.error(stmt.superclass.name, "A class can't inherit itself.");
    }

    if (stmt.superclass != null) {
      currentClass = ClasType.SUBCLASS;
      resolve(stmt.superclass);
    }

    if (stmt.superclass != null) {
      beginScope();
      scopes.peek().put("super", true);
    }

    beginScope();
    scopes.peek().put("this", true);

    for (Stmt.Function method : stmt.methods) {
      FunctionType declaration = FunctionType.METHOD;
      resolveFunction(method, declaration);
    }

    endScope();

    if (stmt.superclass != null) {
      endScope();
    }

    currentClass = enclosingClass;
    return null;
  }

  @Override
  public Void visitExpressionStmt(Stmt.Expression stmt)
  {
    resolve(stmt.expression);
    return null;
  }

  @Override
  public Void visitFunctionStmt(Stmt.Function stmt)
  {
    declare(stmt.name);
    define(stmt.name);

    resolveFunction(stmt, FunctionType.FUNCTION);
    return null;
  }

  @Override
  public Void visitIfStmt(Stmt.If stmt)
  {
    resolve(stmt.condition);
    resolve(stmt.thenBranch);
    if (stmt.elseBranch != null) {
      resolve(stmt.elseBranch);
    }
    return null;
  }

  @Override
  public Void visitPrintStmt(Stmt.Print stmt)
  {
    resolve(stmt.expression);
    return null;
  }

  @Override
  public Void visitReturnStmt(Stmt.Return stmt)
  {
    //TODO: add test
    if (currentFunction == FunctionType.NONE) {
      Lox.error(stmt.keyword, "Can't return from top-level code.");
    }

    if (stmt.value != null) {
      resolve(stmt.value);
    }
    return null;
  }

  @Override
  public Void visitVarStmt(Stmt.Var stmt)
  {
    declare(stmt.name);
    if (stmt.initializer != null) {
      resolve(stmt.initializer);
    }
    define(stmt.name);
    return null;
  }

  @Override
  public Void visitWhileStmt(Stmt.While stmt)
  {
    resolve(stmt.condition);
    resolve(stmt.body);
    return null;
  }
}
