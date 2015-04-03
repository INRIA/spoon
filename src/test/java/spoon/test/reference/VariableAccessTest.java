package spoon.test.reference;

import org.junit.Test;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.visitor.filter.AbstractReferenceFilter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static spoon.test.TestUtils.build;

public class VariableAccessTest {

	@Test
	public void testVariableAccessDeclarationInAnonymousClass() throws Exception {
		CtClass<?> type = build("spoon.test.reference", "FooBar");
		assertEquals("FooBar", type.getSimpleName());

		final CtParameterReference<?> ref = type.getReferences(new AbstractReferenceFilter<CtParameterReference<?>>(CtParameterReference.class) {
			@Override
			public boolean matches(CtParameterReference<?> reference) {
				return "myArg".equals(reference.getSimpleName());
			}
		}).get(0);

		assertNotNull("Parameter can't be null", ref.getDeclaration());
		assertNotNull("Declaring method reference can't be null", ref.getDeclaringExecutable());
		assertNotNull("Declaring type of the method can't be null", ref.getDeclaringExecutable().getDeclaringType());
		assertNotNull("Declaration of declaring type of the method can't be null", ref.getDeclaringExecutable().getDeclaringType().getDeclaration());
		assertNotNull("Declaration of root class can't be null", ref.getDeclaringExecutable().getDeclaringType().getDeclaringType().getDeclaration());
	}
}
