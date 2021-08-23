package records;

public record ExplicitAccessor(int a,String b) {

  public String b() {
    return b;
  }
  
}
