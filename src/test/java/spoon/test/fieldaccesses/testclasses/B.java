package spoon.test.fieldaccesses.testclasses;

public class B {
	private static final int finalField;

	static {
        A.myField = 5;
		finalField = 0;
    }
}