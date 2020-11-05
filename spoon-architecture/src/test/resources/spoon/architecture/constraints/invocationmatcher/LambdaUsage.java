package spoon.architecture.constraints.invocationmatcher;

import java.util.stream.Stream;

public class LambdaUsage {

	private int foo() {
		return 42;
	}

	private void bar() {
		Stream.of(5).forEach(v -> 5 + foo());
		bar();
	}
}
