package spoon.test.refactoring;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.AbstractReferenceFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.refactoring.testclasses.AClass;

import java.util.List;

import static org.junit.Assert.*;

public class RefactoringTest {
	@Test
	public void testRefactoringClassChangeAllCtTypeReferenceAssociatedWithClassConcerned() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {
				"-i", "src/test/java/spoon/test/refactoring/testclasses",
				"-o", "target/spooned/refactoring"
		});
		launcher.run();

		final CtClass<?> aClass = launcher.getFactory().Class().get(AClass.class);
		assertNotNull(aClass);

		launcher.setArgs(new String[] {
				"-i", "src/test/java/spoon/test/refactoring/testclasses",
				"-o", "target/spooned/refactoring",
				"-p", ThisTransformationProcessor.class.getName()
		});
		launcher.run();

		final CtClass<?> classNotAccessible = launcher.getFactory().Class().get(AClass.class);
		assertNull(classNotAccessible);

		final CtClass<?> aClassX = launcher.getFactory().Class().get("spoon.test.refactoring.testclasses.AClassX");
		assertNotNull(aClassX);

		final List<CtTypeReference<?>> references = Query.getReferences(aClassX.getFactory(), new AbstractReferenceFilter<CtTypeReference<?>>(CtTypeReference.class) {
			@Override
			public boolean matches(CtTypeReference<?> reference) {
				return aClassX.getQualifiedName().equals(reference.getQualifiedName());
			}
		});
		assertNotEquals(0, references.size());
		for (CtTypeReference<?> reference : references) {
			assertEquals("AClassX", reference.getSimpleName());
		}
	}

	@Test
	public void testThisInConstructor() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {
				"-i", "src/test/java/spoon/test/refactoring/testclasses",
				"-o", "target/spooned/refactoring"
		});
		launcher.run();
		final CtClass<?> aClass = (CtClass<?>) launcher.getFactory().Type().get(AClass.class);

		final CtInvocation<?> thisInvocation = aClass.getElements(new AbstractFilter<CtInvocation<?>>(CtInvocation.class) {
			@Override
			public boolean matches(CtInvocation<?> element) {
				return element.getExecutable().isConstructor();
			}
		}).get(0);
		assertEquals("this(\"\")", thisInvocation.toString());
	}

	@Test
	public void testThisInConstructorAfterATransformation() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {
				"-i", "src/test/java/spoon/test/refactoring/testclasses",
				"-o", "target/spooned/refactoring",
				"-p", ThisTransformationProcessor.class.getName()
		});
		launcher.run();
		final CtClass<?> aClassX = (CtClass<?>) launcher.getFactory().Type().get("spoon.test.refactoring.testclasses.AClassX");
		final CtInvocation<?> thisInvocation = aClassX.getElements(new AbstractFilter<CtInvocation<?>>(CtInvocation.class) {
			@Override
			public boolean matches(CtInvocation<?> element) {
				return element.getExecutable().isConstructor();
			}
		}).get(0);
		assertEquals("this(\"\")", thisInvocation.toString());
	}

	@Test
	public void testTransformedInstanceofAfterATransformation() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {
				"-i", "src/test/java/spoon/test/refactoring/testclasses",
				"-o", "target/spooned/refactoring",
				"-p", ThisTransformationProcessor.class.getName()
		});
		launcher.run();
		final CtClass<?> aClassX = (CtClass<?>) launcher.getFactory().Type().get("spoon.test.refactoring.testclasses.AClassX");

		final CtBinaryOperator<?> instanceofInvocation = aClassX.getElements(new TypeFilter<CtBinaryOperator<?>>(CtBinaryOperator.class)).get(0);
		assertEquals(BinaryOperatorKind.INSTANCEOF, instanceofInvocation.getKind());
		assertEquals("o", instanceofInvocation.getLeftHandOperand().toString());
		assertEquals("spoon.test.refactoring.testclasses.AClassX", instanceofInvocation.getRightHandOperand().toString());
	}
}
