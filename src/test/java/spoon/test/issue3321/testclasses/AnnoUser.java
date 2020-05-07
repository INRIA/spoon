package spoon.test.issue3321.testclasses;

public class AnnoUser {
	public void m1(@MyAnno String p) {}
	public void m2(@OtherAnno String p) {}
	public void m3(@Ambiguous String p) {}
}