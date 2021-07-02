package extendsStaticInnerType;

public class BarBaz implements FooBar.Crashy {

  @Override
  public String foo() {
    return "bar!";
  }

}