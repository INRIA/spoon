package records;

public record WithStaticInitializer(int i, String s) {
  static {
    System.out.println("Hello World");
  }
  static {
    if (Math.random() < 0.5) {
      System.out.println("A");
    } else {
      System.out.println("B");
    }
  }
}
