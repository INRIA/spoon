package spoon.test.reference;

import org.junit.Test;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.reference.testclasses.Mole;
import spoon.test.reference.testclasses.Parameter;
import spoon.testing.utils.ModelUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AnnotationFieldReferenceTest {
	@Test
	public void testAnnotationFieldReference() throws Exception {
		final Factory factory = ModelUtils.build(Parameter.class, Mole.class);
		final CtMethod<Object> make = factory.Class().get(Mole.class).getMethod("make", factory.Type().createReference(Parameter.class));
		final CtInvocation<?> annotationInv = make.getElements(new TypeFilter<CtInvocation<?>>(CtInvocation.class)).get(0);
		final CtExecutable<?> executableDeclaration = annotationInv.getExecutable().getExecutableDeclaration();
		assertNotNull(executableDeclaration);
		final CtMethod<?> value = factory.Annotation().get(Parameter.class).getMethod("value");
		assertNotNull(value);
		assertEquals(value.getSimpleName(), executableDeclaration.getSimpleName());
		assertEquals(value.getType(), executableDeclaration.getType());
	}
}
