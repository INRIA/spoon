package records;

public record ExplicitAccessor(String b) {

  public String b() {
    return b;
  }
  
}
