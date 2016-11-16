package spoon.test.intercession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import spoon.Launcher;
import spoon.processing.FactoryAccessor;
import spoon.reflect.ast.IntercessionScanner;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class IntercessionContractTest {

	@Parameterized.Parameters(name = "{0}")
	public static Collection<Object[]> data() throws Exception {
		final Launcher launcher = new Launcher();
		final Factory factory = launcher.getFactory();
		launcher.getEnvironment().setNoClasspath(true);
		// interfaces.
		launcher.addInputResource("./src/main/java/spoon/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/reflect/reference");
		// implementations.
		launcher.addInputResource("./src/main/java/spoon/support/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/reference");
		launcher.buildModel();

		final List<Object[]> values = new ArrayList<>();
		new IntercessionScanner(launcher.getFactory()) {
			@Override
			protected boolean isToBeProcessed(CtMethod<?> candidate) {
				return (candidate.getSimpleName().startsWith("set") //
						|| candidate.getSimpleName().startsWith("add")) //
						&& candidate.hasModifier(ModifierKind.PUBLIC) //
						&& takeSetterForCtElement(candidate) //
						&& avoidInterfaces(candidate) //
						&& avoidThrowUnsupportedOperationException(candidate);
			}

			@Override
			protected void process(CtMethod<?> element) {
				values.add(new Object[] { getDeclaringClassConcrete(element), element.getReference().getActualMethod() });
			}

			private Class<?> getDeclaringClassConcrete(CtMethod<?> element) {
				final CtType<?> declaringType = element.getDeclaringType();
				if (!declaringType.hasModifier(ModifierKind.ABSTRACT)) {
					return declaringType.getActualClass();
				}
				final List<CtTypeReference<?>> superClasses = getSuperClassesOf(declaringType);
				superClasses.add(declaringType.getReference());
				final List<CtClass<?>> elements = Query.getElements(factory, new TypeFilter<CtClass<?>>(CtClass.class) {
					@Override
					public boolean matches(CtClass<?> element) {
						return super.matches(element)
								// Want a concrete class.
								&& !element.hasModifier(ModifierKind.ABSTRACT)
								// Class can't be one of the superclass (or itself) of the declaring class.
								&& !superClasses.contains(element.getReference())
								// Current class have in its super class hierarchy the given declaring class.
								&& getSuperClassesOf(element).contains(declaringType.getReference());
					}
				});
				if (elements.size() <= 0) {
					fail("Can't have an abstract class without any concrete sub class. Error detected with " + declaringType.getQualifiedName());
				}
				return takeFirstOneCorrect(element, elements);
			}

			private Class<?> takeFirstOneCorrect(CtMethod<?> element, List<CtClass<?>> potentials) {
				for (CtClass<?> potential : potentials) {
					final CtMethod<?> method = potential.getMethod(
							element.getType(), element.getSimpleName(),
							element.getParameters().stream().map(CtTypedElement::getType).toArray(CtTypeReference[]::new));
					if (method == null) {
						continue;
					}
					if (avoidThrowUnsupportedOperationException(method)) {
						return potential.getActualClass();
					}
				}
				// Method don't declared in sub classes.
				return potentials.get(0).getActualClass();
			}

			private List<CtTypeReference<?>> getSuperClassesOf(CtType<?> declaringType) {
				final List<CtTypeReference<?>> superClasses = new ArrayList<>();
				CtTypeReference<?> declaringTypeReference = declaringType.getReference();
				while (declaringTypeReference.getSuperclass() != null) {
					superClasses.add(declaringTypeReference.getSuperclass());
					declaringTypeReference = declaringTypeReference.getSuperclass();
				}
				return superClasses;
			}
		}.scan(launcher.getModel().getRootPackage());
		return values;
	}

	@Parameterized.Parameter(0)
	public Class<?> declaringClass;

	@Parameterized.Parameter(1)
	public Method toTest;

	@Test
	public void testContract() throws Throwable {
		Factory factory = new FactoryImpl(new DefaultCoreFactory(),new StandardEnvironment());
		try {
			Object element = declaringClass.newInstance();
			if (element instanceof FactoryAccessor) {
				((FactoryAccessor) element).setFactory(factory);
			}
			// we invoke the setter
			toTest.invoke(element, new Object[] { null });
		} catch (NullPointerException e) {
			fail("Shouldn't throw NPE.");
		} catch (InvocationTargetException e) {
			if (!(e.getTargetException() instanceof UnsupportedOperationException)) {
				throw new RuntimeException("Unexpected exception happened with " + toTest.getName() + " in " + declaringClass.getName(), e.getTargetException());
			}
		} catch (Exception e) {
			throw new RuntimeException("Unexpected exception happened with " + toTest.getName() + " in " + declaringClass.getName(), e);
		}
	}

}
