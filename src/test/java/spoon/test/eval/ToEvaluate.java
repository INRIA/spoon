package spoon.test.eval;

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

}
