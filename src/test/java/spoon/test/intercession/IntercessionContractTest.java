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
import static spoon.test.parent.ParentContractTest.createCompatibleObject;

// contract: one can call all setters with null as parameter (no problem with parent)
@RunWith(Parameterized.class)
public class IntercessionContractTest {

	@Parameterized.Parameters(name = "{1}")
	public static Collection<Object[]> data() throws Exception {
		final Launcher launcher = new Launcher();
		final Factory factory = launcher.getFactory();
		launcher.getEnvironment().setNoClasspath(true);
		// all metamodel interfaces.
		launcher.addInputResource("./src/main/java/spoon/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/reflect/reference");
		launcher.buildModel();

		final List<Object[]> values = new ArrayList<>();
		new IntercessionScanner(launcher.getFactory()) {
			@Override
			protected boolean isToBeProcessed(CtMethod<?> candidate) {
				return (candidate.getSimpleName().startsWith("set") //
						|| candidate.getSimpleName().startsWith("add")) //
						&& takeSetterForCtElement(candidate); //
			}

			@Override
			protected void process(CtMethod<?> element) {
				values.add(new Object[] { createCompatibleObject(element.getDeclaringType().getReference()), element.getReference().getActualMethod() });
			}
		}.scan(launcher.getModel().getRootPackage());
		return values;
	}

	@Parameterized.Parameter(0)
	public Object instance;

	@Parameterized.Parameter(1)
	public Method toTest;

	@Test
	public void testContract() throws Throwable {
		Factory factory = new FactoryImpl(new DefaultCoreFactory(),new StandardEnvironment());
		Object element = instance;
		if (element instanceof FactoryAccessor) {
			((FactoryAccessor) element).setFactory(factory);
		}
		// we invoke the setter
		toTest.invoke(element, new Object[] { null });
	}

}
