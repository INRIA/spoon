package spoon.test.template.testclasses;


public class C {

	public static void main(String[] args) {
		try {
			System.out.print(".");
			new C();
			System.out.print(".");
			new C();
			System.out.print(".");
			new C();
			System.out.print(".");
			new C();
			System.out.print(".");
			new C();
			System.out.print(".");
			new C();
		} catch (RuntimeException e) {
			System.out.flush();
			System.err.println(e.getMessage());
		}
	}

}
