package spoon.architecture.constraints.invocationmatcher;

public class LocalUsage {


	private int foo() {
		return 42;
	}

	private void bar() {
		int a = foo();
		bar();
	}
}
