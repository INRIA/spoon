package spoon.bliblablub;

import org.assertj.core.api.AbstractAssert;
import org.junit.jupiter.api.Test;
import spoon.reflect.declaration.CtShadowable;
import spoon.reflect.declaration.CtType;
import spoon.testing.assertions.SpoonAssert;

public class AssertJDings {

	@Test
	void xyz() {
		assertThat((CtType<?>) null).isShadow().hasSimpleName("aaaa").isShadow().hasSimpleName("aaabasd");
	}

	public static CtTypeAssert assertThat(CtType<?> type) {
		return new CtTypeAssert(type);
	}

	public static class CtTypeAssert extends AbstractAssert<CtTypeAssert, CtType<?>>
		implements
		CtTypeAssertInterface<CtTypeAssert, CtType<?>>,
		CtShadowableAssertInterface<CtTypeAssert, CtType<?>>
	{

		protected CtTypeAssert(CtType<?> ctType) {
			super(ctType, CtTypeAssert.class);
		}

		@Override
		public CtTypeAssert self() {
			return this;
		}

		@Override
		public CtType<?> actual() {
			return actual;
		}

		@Override
		public void failWithMessage(String errorMessage, Object... arguments) {
			super.failWithMessage(errorMessage, arguments);
		}
	}

	private interface CtShadowableAssertInterface<A extends AbstractAssert<A, W>, W extends CtShadowable> extends SpoonAssert<A, W> {

		default A isShadow() {
			self().isNotNull();
			assert actual().isShadow() : "is shadow";
			return self();
		}

	}

	private interface CtTypeAssertInterface<A extends AbstractAssert<A, W>, W extends CtType<?>> extends SpoonAssert<A, W> {
		default A hasSimpleName(String simpleName) {
			self().isNotNull();
			if (!actual().getSimpleName().equals(simpleName)) {
				failWithMessage("Expected parent to be <%s> but was <%s>", "parent", actual().getParent());
			}
			return self();
		}

	}

}
