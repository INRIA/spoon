package spoon.test.template.testclasses.match;

public class MatchModifiers {


	public void matcher1() {
	}
	
	public static void publicStaticMethod() {
	}
	
	void packageProtectedMethodWithParam(int a, MatchModifiers me) {
	}

	private void withBody() {
		this.getClass();
		System.out.println();
	}
	
	int noMatchBecauseReturnsInt() {return 0;}
}
