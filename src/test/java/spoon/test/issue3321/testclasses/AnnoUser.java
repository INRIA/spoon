package spoon.test.issue3321.testclasses;

public class AnnoUser {
	public void m1(@MyAnno String p) {}
	public void m2(@OtherAnno String p) {}
	public void m3(@Ambiguous String p) {}


	public void m4(@MyAnno /* dfd */ String p) {}
	public void m5(@MyAnno /* d@d */ String p) {}
	public void m6(@MyAnno @OtherAnno @Ambiguous String p) {}
}