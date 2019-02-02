package spoon.test.eval.testclasses;

import spoon.reflect.declaration.CtElement;

public class ToEvaluate {

	static final String S1 = "S1";
	static final String S2 = "S2";
	static final String S1S2_1 = S1 + "S2";
	static final String S1S2_2 = S1 + S2;
	static final int I1 = 10;

	public static void testStrings() {
		// all the following code will be removed by the partial evaluator
		if (!S1S2_1.equals(S1 + S2)) {
			System.out.println("dead code");
		}
		if (!S1S2_1.equals("S1S2")) {
			System.out.println("dead code");
		}
		if (!S1S2_2.equals("S1S2")) {
			System.out.println("dead code");
		}
		if (!S1S2_1.equals(S1S2_2)) {
			System.out.println("dead code");
		}
	}

	@SuppressWarnings("unused")
	public static void testInts() {
		// all the following code will be removed by the partial evaluator
		if (I1 + 1 != 11) {
			System.out.println("dead code");
		}
	}

	public static void testArray() {
		// all the following code will be removed by the partial evaluator
		if (new String[]{"a", null, "b"}.length == 11) {
			System.out.println("dead code");
		}
	}

	public static void testDoNotSimplify(String className, String methodName) {
		// this code must not be simplified
		java.lang.System.out.println(((("enter: " + className) + " - ") + methodName));
	}

	public static <U> U testDoNotSimplifyCasts(CtElement element) {
		// this code must not be simplified
		return ((U) ((Object) (castTarget(element).getClass())));
	}

	private static <T> T castTarget(CtElement element) {
		return (T) element;
	}

	private static String tryCatchAndStatement(CtElement element) {
		try {
			element.getClass();
		} catch (RuntimeException e) {
			throw e;
		}
		return "This must not be removed";
	}

	private static String simplifyOnlyWhenPossible(CtElement element) {
		//this must not be simplified because literal is not a statement.
		ToEvaluate.class.getName();
		//this must not be simplified because ClassLoader instance is not a literal
		System.out.println(ToEvaluate.class.getClassLoader());
		//this can be simplified because return expects expression
		return ToEvaluate.class.getName();
	}
	int foo(int x) {
		return x+1;
	}
	final int ff=3;
	int foo2() {
		return ff+1;
	}
	final Class ff3=String.class;
	Class foo3() {
		return ff3;
	}
}
