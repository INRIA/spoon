package spoon.testing.assertions;

import java.util.function.Consumer;

public interface SpoonAssert<SELF, ACTUAL> {

	SELF self();

	ACTUAL actual();

	default SELF nested(Consumer<SELF> checks) {
		checks.accept(self());
		return self();
	}

	void failWithMessage(String errorMessage, Object... arguments);
}
