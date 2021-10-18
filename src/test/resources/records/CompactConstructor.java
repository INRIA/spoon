package records;

public record Rational(int num, int denom) { 
  private static int gcd(int a, int b) { 
  if (b == 0) return Math.abs(a); 
  else return gcd(b, a % b); 
  } 
  
  Rational { 
    int gcd = gcd(num, denom); 
    num /= gcd; 
    denom /= gcd; 
    } 
  }
