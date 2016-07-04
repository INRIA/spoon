package spoon.test.reference;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtArrayWrite;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.filter.AbstractReferenceFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.reference.testclasses.Pozole;
import spoon.testing.utils.ModelUtils;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static spoon.testing.utils.ModelUtils.build;

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

	@Test
	public void name() throws Exception {
		final CtType<Pozole> aPozole = ModelUtils.buildClass(Pozole.class);
		final CtMethod<Object> m2 = aPozole.getMethod("m2");
		final CtArrayWrite<?> ctArrayWrite = m2.getElements(new TypeFilter<CtArrayWrite<?>>(CtArrayWrite.class)).get(0);
		final CtLocalVariable expected = m2.getElements(new TypeFilter<CtLocalVariable>(CtLocalVariable.class)).get(0);

		assertEquals(expected, ((CtVariableAccess) ctArrayWrite.getTarget()).getVariable().getDeclaration());
	}

	@Test
	public void testDeclarationOfVariableReference() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/Foo2.java");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.buildModel();

		launcher.getModel().getElements(new TypeFilter<CtVariableReference>(CtVariableReference.class) {
			@Override
			public boolean matches(CtVariableReference element) {
				try {
					element.clone().getDeclaration();
				} catch (NullPointerException e) {
					fail("Fail with " + element.getSimpleName() + " declared in " + element.getParent().getShortRepresentation());
				}
				return super.matches(element);
			}
		});
	}

	@Test
	public void testDeclaringTypeOfALambdaReferencedByParameterReference() {
		final spoon.Launcher launcher = new spoon.Launcher();
		launcher.addInputResource("src/test/resources/noclasspath/Foo3.java");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.getEnvironment().setComplianceLevel(8);
		launcher.buildModel();

		launcher.getModel().getElements(new TypeFilter<CtExecutable<?>>(CtExecutable.class) {
			@Override
			public boolean matches(CtExecutable<?> exec) {
				final List<CtParameterReference<?>> guiParams = exec.getParameters().stream().map(CtParameter::getReference).collect(Collectors.toList());

				if (guiParams.size() != 1) {
					return false;
				}

				final CtParameterReference<?> param = guiParams.get(0);

				exec.getBody().getElements(new TypeFilter<CtParameterReference<?>>(CtParameterReference.class) {
					@Override
					public boolean matches(CtParameterReference<?> p) {
						assertEquals(p, param);
						return super.matches(p);
					}
				});

				return super.matches(exec);
			}
		});
	}
}
