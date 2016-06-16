package spoon.testing;

import org.junit.Test;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;

import static spoon.testing.Assert.assertThat;
import static spoon.testing.utils.ModelUtils.buildNoClasspath;
import static spoon.testing.utils.ModelUtils.createFactory;

public class CtElementAssertTest {
	public int i;

	@Test
	public void testEqualityBetweenTwoCtElement() throws Exception {
		final CtType<CtElementAssertTest> type = buildNoClasspath(CtElementAssertTest.class).Type().get(CtElementAssertTest.class);
		final Factory factory = createFactory();
		final CtField<Integer> expected = factory.Core().createField();
		expected.setSimpleName("i");
		expected.setType(factory.Type().INTEGER_PRIMITIVE);
		expected.addModifier(ModifierKind.PUBLIC);
		CtField<?> f = type.getField("i");
		assertThat(f).isEqualTo(expected);
	}

	@Test
	public void testEqualityBetweenACtElementAndAString() throws Exception {
		final CtType<CtElementAssertTest> type = buildNoClasspath(CtElementAssertTest.class).Type().get(CtElementAssertTest.class);
		assertThat(type.getField("i")).isEqualTo("public int i;");
	}

	@Test(expected = AssertionError.class)
	public void testEqualityBetweenTwoCtElementWithTypeDifferent() throws Exception {
		assertThat(createFactory().Core().createAnnotation()).isEqualTo(createFactory().Core().createBlock());
	}

	@Test(expected = AssertionError.class)
	public void testEqualityBetweenTwoCtElementWithTheSameSignatureButNotTheSameContent() throws Exception {
		assertThat(buildNoClasspath(CtElementAssertTest.class).Type().get(CtElementAssertTest.class)).isEqualTo(createFactory().Class().create(CtElementAssertTest.class.getName()));
	}

	@Test(expected = AssertionError.class)
	public void testEqualityBetweenTwoDifferentCtElement() throws Exception {
		class String {
		}
		final Factory build = buildNoClasspath(CtElementAssertTest.class);
		final CtFieldAccess<Class<String>> actual = build.Code().createClassAccess(build.Type().<String>get(String.class).getReference());
		final CtFieldAccess<Class<java.lang.String>> expected = createFactory().Code().createClassAccess(createFactory().Type().STRING);
		assertThat(actual).isEqualTo(expected);
	}
}
