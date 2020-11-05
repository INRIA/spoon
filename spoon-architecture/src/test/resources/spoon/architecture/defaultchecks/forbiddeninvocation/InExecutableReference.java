package spoon.architecture.defaultchecks;

import java.util.function.Consumer;
public class ExecutableReference {

	void bar() {
		java.util.function.Consumer<java.lang.String> foo = java.lang.System.out::println;
	}
}
