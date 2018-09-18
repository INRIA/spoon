package spoon.test.imports.testclasses;


public class JavaLongUse {

	public class Long {

	}

	public static long method() {
		return java.lang.Long.parseLong("10000");
	}

	public static void main(String[] args) {
		System.out.println(method());
	}
}