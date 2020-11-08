package spoon.support.visitor.java.testclasses;

public class UnsatisfiedLinkErrorInStaticInit {

  public static int VALUE = 1; // because of this field
  
  static {
    System.load("not found path!");
  }
}
