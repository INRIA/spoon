package spoon.test.visibility.testclasses;

public class Float {
	public static float sum(float a, float b) {
		return a + b;
	}

	public static java.lang.Float aMethodNotInJavaLangFloatClass(String param1, String param2) {
		return 0.0f;
	}

	public void aMethodNotStatic() {
	}
}
