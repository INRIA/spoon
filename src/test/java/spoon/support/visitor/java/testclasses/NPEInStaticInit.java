package spoon.support.visitor.java.testclasses;

public class NPEInStaticInit {

  private static class InnerClass {
    public void doSmt(){ }
  }

  public static int VALUE = 1; // because of this field
  
  static {
    System.out.println("This text will be printed"); // NOTE
  }

  public static InnerClass someObject = null;
  
  static {
    someObject.doSmt();      // NPE !!!
  }
}
