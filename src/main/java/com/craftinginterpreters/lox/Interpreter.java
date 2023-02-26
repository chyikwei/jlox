package com.craftinginterpreters.lox;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void>
{
  private PrintStream printStream;
  final Environment globals;
  private Environment environment;
  private final Map<Expr, Integer> locals;

  public Interpreter() {
    this(System.out);
  }

  public Interpreter(OutputStream out) {
    this.printStream = new PrintStream(out);
    this.globals = new Environment();
    this.environment = globals;
    this.locals = new HashMap<>();

    globals.define("clock", new LoxCallable() {
      @Override
      public int arity()
      {
        return 0;
      }

      @Override
      public Object call(Interpreter interpreter, List<Object> arguments)
      {
        return (double) System.currentTimeMillis() / 1000.0;
      }

      @Override
      public String toString() {
        return "<native fn>";
      };
    });
  }

  @Override
  public Object visitAssignExpr(Expr.Assign expr)
  {
    Object value = evaluate(expr.value);

    Integer distance = locals.get(expr);
    if (distance != null) {
      environment.assignAt(distance, expr.name, value);
    } else {
      globals.assign(expr.name, value);
    }

    return value;
  }

  @Override
  public Object visitBinaryExpr(Expr.Binary expr)
  {
    Object left = evaluate(expr.left);
    Object right = evaluate(expr.right);

    switch (expr.operator.type()) {
      case GREATER:
        checkNumberOperands(expr.operator, left, right);
        return (double) left > (double) right;

      case GREATER_EQUAL:
        checkNumberOperands(expr.operator, left, right);
        return (double) left >= (double) right;

      case LESS:
        checkNumberOperands(expr.operator, left, right);
        return (double) left < (double) right;

      case LESS_EQUAL:
        checkNumberOperands(expr.operator, left, right);
        return (double) left <= (double) right;
      case EQUAL_EQUAL:
        return isEqual(left, right);
      case BANG_EQUAL:
        return !isEqual(left, right);
      case MINUS:
        checkNumberOperands(expr.operator, left, right);
        return (double) left - (double) right;
      case PLUS:
        if (left instanceof Double && right instanceof Double) {
          return (double) left + (double) right;
        }

        if (left instanceof String && right instanceof String) {
          return (String) left + (String) right;
        }
        throw new RunTimeError(expr.operator, "Operands must be two numbers or two strings");
      case SLASH:
        checkNumberOperands(expr.operator, left, right);
        return (double) left / (double) right;
      case STAR:
        checkNumberOperands(expr.operator, left, right);
        return (double) left * (double) right;
    }
    // unreachable
    return null;
  }

  @Override
  public Object visitCallExpr(Expr.Call expr)
  {
    Object callee = evaluate(expr.callee);
    List<Object> arguments = new ArrayList<>();
    for (Expr argument : expr.arguments) {
      arguments.add(evaluate(argument));
    }

    if (!(callee instanceof LoxCallable)) {
      throw new RunTimeError(expr.paren, "Can only call functions and classes");
    }

    LoxCallable function = (LoxCallable) callee;
    if (function.arity() != arguments.size()) {
      throw new RunTimeError(expr.paren, "Expected "
          + function.arity() + " arguments but got"
          + arguments.size() + ".");
    }

    return function.call(this, arguments);
  }

  @Override
  public Object visitGetExpr(Expr.Get expr)
  {
    Object object = evaluate(expr.object);
    if (object instanceof LoxInstance) {
      return ((LoxInstance) object).get(expr.name);
    }
    throw new RunTimeError(expr.name, "Only instances have properties.");

  }

  @Override
  public Object visitGroupingExpr(Expr.Grouping expr)
  {
    return evaluate(expr.expression);
  }

  @Override
  public Object visitLiteralExpr(Expr.Literal expr)
  {
    return expr.value;
  }

  @Override
  public Object visitLogicalExpr(Expr.Logical expr)
  {
    Object left = evaluate(expr.left);

    if (expr.operator.type() == TokenType.OR) {
      if (isTruthy(left)) {
        return left;
      }
    } else {
      if (!isTruthy(left)) {
        return left;
      }
    }
    return evaluate(expr.right);
  }

  @Override
  public Object visitSetExpr(Expr.Set expr)
  {
    Object object = evaluate(expr.object);
    if (!(object instanceof LoxInstance)) {
      throw new RunTimeError(expr.name, "Only instances have fields");
    }
    Object value = evaluate(expr.value);
    ((LoxInstance) object).set(expr.name, value);
    return value;
  }

  @Override
  public Object visitUnaryExpr(Expr.Unary expr)
  {
    Object right = evaluate(expr.right);
    switch (expr.operator.type()) {
      case BANG:
        return !isTruthy(right);
      case MINUS:
        checkNumberOperand(expr.operator, right);
        return -(double) right;
    }

    //unreachable
    return null;
  }

  @Override
  public Object visitVariableExpr(Expr.Variable expr)
  {
    return lookUpVariable(expr.name, expr);
  }

  private Object lookUpVariable(Token name, Expr expr) {
    Integer distance = locals.get(expr);
    if (distance != null) {
      return environment.getAt(distance, name.lexeme());
    } else {
      return globals.get(name);
    }
  }

  Object evaluate(Expr expr) {
    return expr.accept(this);
  }

  void resolve(Expr expr, int depth) {
    locals.put(expr, depth);
  }

  Object evaluate(Stmt statement) {
    return statement.accept(this);
  }

  private boolean isTruthy(Object object)
  {
    if (object == null) {
      return  false;
    }
    if (object instanceof Boolean) {
      return (boolean) object;
    }
    return true;
  }

  private boolean isEqual(Object a, Object b) {
    if (a == null && b == null) {
      return true;
    }
    if (a == null) {
      return false;
    }
    return a.equals(b);
  }

  private void checkNumberOperand(Token operator, Object operand) {
    if (operand instanceof Double) {
      return;
    }
    throw new RunTimeError(operator, "Operand must be a number.");
  }

  private void checkNumberOperands(Token operator, Object left, Object right) {
    if (left instanceof Double && right instanceof Double) {
      return;
    }
    throw new RunTimeError(operator, "Operands must be numbers.");
  }

  public void interpret(List<Stmt> statements) {
    try {
      for (Stmt statement : statements) {
        evaluate(statement);
      }
    } catch (RunTimeError error) {
      Lox.runtimeError(error);
    }
  }

  private String stringify(Object object) {
    if (object == null) {
      return "nil";
    }

    if (object instanceof Double) {
      String text = object.toString();
      if (text.endsWith(".0")) {
        text = text.substring(0, text.length() - 2);
      }
      return text;
    }
    return object.toString();
  }

  @Override
  public Void visitBlockStmt(Stmt.Block stmt)
  {
    evaluateBlock(stmt.statements, new Environment(environment));
    return null;
  }

  @Override
  public Void visitClassStmt(Stmt.Class stmt)
  {
    environment.define(stmt.name.lexeme(), null);

    Map<String, LoxFunction> methods = new HashMap<>();
    for (Stmt.Function method : stmt.methods) {
      LoxFunction function = new LoxFunction(method, environment);
      methods.put(method.name.lexeme(), function);
    }
    LoxClass klass = new LoxClass(stmt.name.lexeme(), methods);
    environment.assign(stmt.name, klass);
    return null;
  }

  void evaluateBlock(List<Stmt> statements, Environment environment) {
    // ptr to initial environment
    Environment previous = this.environment;
    try {
      this.environment = environment;
      for (Stmt statement : statements) {
        evaluate(statement);
      }
    } finally {
      this.environment = previous;
    }
  }

  @Override
  public Void visitExpressionStmt(Stmt.Expression stmt)
  {
    evaluate(stmt.expression);
    return null;
  }

  @Override
  public Void visitFunctionStmt(Stmt.Function stmt)
  {
    LoxFunction function = new LoxFunction(stmt, environment);
    environment.define(stmt.name.lexeme(), function);
    return null;
  }

  @Override
  public Void visitIfStmt(Stmt.If stmt)
  {
    if (isTruthy(evaluate(stmt.condition))) {
      evaluate(stmt.thenBranch);
    } else if (stmt.elseBranch != null) {
      evaluate(stmt.elseBranch);
    }
    return null;
  }

  @Override
  public Void visitPrintStmt(Stmt.Print stmt)
  {
    Object value = evaluate(stmt.expression);
    printStream.println(stringify(value));
    return null;
  }

  @Override
  public Void visitReturnStmt(Stmt.Return stmt)
  {
    Object value = null;
    if (stmt.value != null) {
      value = evaluate(stmt.value);
    }
    throw new Return(value);
  }

  @Override
  public Void visitVarStmt(Stmt.Var stmt)
  {
    Object value = null;
    if (stmt.initializer != null) {
      value = evaluate(stmt.initializer);
    }
    environment.define(stmt.name.lexeme(), value);
    return null;
  }

  @Override
  public Void visitWhileStmt(Stmt.While stmt)
  {
    while (isTruthy(evaluate(stmt.condition))) {
      evaluate(stmt.body);
    }
    return null;
  }
}
