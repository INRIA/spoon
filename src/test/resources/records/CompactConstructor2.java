package records;

public record CompactConstructor2(int num, int denom) { 
  private static int gcd(int a, int b) { 
    if (b == 0) return Math.abs(a); 
    else return gcd(b, a % b); 
  } 
  
  public CompactConstructor2 { 
    int gcd = gcd(num, denom); 
    num /= gcd; 
    denom /= gcd;
  } 
}
