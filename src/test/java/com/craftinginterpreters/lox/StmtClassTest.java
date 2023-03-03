package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StmtClassTest
{
  @Test
  void testPrintClass() {
    String input = """
    class TestClass {
      test() {
        return "test";
      }
    }
    print TestClass;
    """;
    String output = PrintOutputHelper.printOutput(input);
    assertEquals("TestClass", output);
  }

  @Test
  void testClassInstance() {
    String input = """
    class TestClass {}
    var t = TestClass();
    print t;
    """;
    String output = PrintOutputHelper.printOutput(input);
    assertEquals("TestClass instance", output);
  }

  @Test
  void testSetGetField() {
    String input = """
    class TestClass {}
    var t = TestClass();
    t.a = "123";
    print t.a;
    """;
    String output = PrintOutputHelper.printOutput(input);
    assertEquals("123", output);
  }

  @Test
  void testMethod() {
    String input = """
    class Bacon {
      eat() {
        print "Crunch!";
      }
    }
    Bacon().eat();
    """;
    String output = PrintOutputHelper.printOutput(input);
    assertEquals("Crunch!", output);
  }

  @Test
  void testPrintThis() {
    String input = """
    class Egotist {
      speak() {
        print this;
      }
    }
    var method = Egotist().speak;
    method();
    """;
    String output = PrintOutputHelper.printOutput(input);
    assertEquals("Egotist instance", output);
  }

  @Test
  void testMethodReference() {
    String input = """
    class Cake {
      taste() {
        print this.flavor + " cake is good!";
      }
    }
    var cake = Cake();
    cake.flavor = "chocolate";
    cake.taste();
    """;
    String output = PrintOutputHelper.printOutput(input);
    assertEquals("chocolate cake is good!", output);
  }

  @Test
  void testMethodInheritance() {
    String input = """
    class Doughnut {
      cook() {
        print "cook";
      }
    }
    class BostonCream < Doughnut {}
    
    BostonCream().cook();
    """;
    String output = PrintOutputHelper.printOutput(input);
    assertEquals("cook", output);
  }

  @Test
  void testSuperClassMethod() {
    String input = """
    class Doughnut {
      cook() {
        print "cook";
      }
    }
    class BostonCream < Doughnut {
      cook() {
        super.cook();
        print "more!";
      }
    }
    
    BostonCream().cook();
    """;
    String output = PrintOutputHelper.printOutput(input);
    assertEquals("cook\nmore!", output);
  }

  @Test
  void testSuperBinding() {
    String input = """
    class A {
      method() {
        print "method A";
      }
    }
    class B < A {
      method() {
        print "method B";
      }
      
      test() {
        super.method();
      }
    }
    
    class C < B {}
    C().test();
    """;
    String output = PrintOutputHelper.printOutput(input);
    assertEquals("method A", output);
  }
}
