package fr.inria.gforge.spoon.analysis;

/**
 * The Class Testcase2.
 *
 * testcase to show NullPointerException
 */
public class Testcase2 {

  private class A {

  }

  private class B extends A {

    /**
     * Instantiates a new b.
     */
    public B() {
      super();
    }
  }

  public static void main(String[] args) {
    Testcase2 t = new Testcase2();
    t.test();
  }

  public String test() {
    var b = new B();
    return b.toString();
  }
}
