package spoon.testing.assertions;

public interface SpoonAssert<SELF, ACTUAL> {

	SELF self();

	ACTUAL actual();

	void failWithMessage(String errorMessage, Object... arguments);
}
