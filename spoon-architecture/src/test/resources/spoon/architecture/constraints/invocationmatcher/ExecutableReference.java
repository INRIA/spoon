package spoon.architecture.constraints.invocationmatcher;

import java.util.function.Supplier;

public class ExecutableReference {

	Supplier<Integer> foo = this::bar;

	Integer bar() {
		return 42;
	}
}
