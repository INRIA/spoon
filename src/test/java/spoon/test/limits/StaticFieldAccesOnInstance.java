package spoon.test.limits;

public class StaticFieldAccesOnInstance {

	public static String test = "";

	@SuppressWarnings("static-access")
	void method() {
		StaticFieldAccesOnInstance test2 = new StaticFieldAccesOnInstance();
		System.out.println(StaticFieldAccesOnInstance.test);
		System.out.println(test2.test);
	}

}
