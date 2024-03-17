package spoon.testing.assertions;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public interface SpoonAssert<SELF, ACTUAL> {

	SELF self();

	ACTUAL actual();

	default SELF nested(Consumer<SELF> checks) {
		checks.accept(self());
		return self();
	}

	default SELF isInstanceOf(Class<?> type) {
		assertThat(actual()).isInstanceOf(type);
		return self();
	}

	default SELF isEqualTo(Object expected) {
		assertThat(actual()).isEqualTo(expected);
		return self();
	}

	void failWithMessage(String errorMessage, Object... arguments);
}
