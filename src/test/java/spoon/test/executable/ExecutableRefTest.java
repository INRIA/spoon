package spoon.test.executable;

import org.junit.Assert;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.InvocationFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.executable.testclasses.Pozole;
import spoon.testing.utils.ModelUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.canBeBuilt;

public class ExecutableRefTest {

	@Test
	public void methodTest() throws Exception {
		CtAbstractInvocation<?> ctAbstractInvocation = this.getInvocationFromMethod("testMethod");
		Assert.assertTrue(ctAbstractInvocation instanceof CtInvocation<?>);

		CtExecutableReference<?> executableReference = ctAbstractInvocation.getExecutable();
		Assert.assertNotNull(executableReference);

		Method method = executableReference.getActualMethod();
		Assert.assertNotNull(method);

		assertEquals("Hello World",
				method.invoke(null, ((CtLiteral<?>) ctAbstractInvocation.getArguments().get(0)).getValue()));
	}

	@Test
	public void constructorTest() throws Exception {
		CtAbstractInvocation<?> ctAbstractInvocation = this.getInvocationFromMethod("testConstructor");
		Assert.assertTrue(ctAbstractInvocation instanceof CtConstructorCall<?>);

		CtExecutableReference<?> executableReference = ctAbstractInvocation.getExecutable();
		Assert.assertNotNull(executableReference);

		Constructor<?> constructor = executableReference.getActualConstructor();
		Assert.assertNotNull(constructor);

		assertEquals("Hello World",
				constructor.newInstance(((CtLiteral<?>) ctAbstractInvocation.getArguments().get(0)).getValue()));
	}

	@Test
	public void testGetActualClassTest() throws Exception {
		Factory factory = build(ExecutableRefTestSource.class, MyIntf.class);

		CtMethod<?> method = factory.Class().get(ExecutableRefTestSource.class).getMethod("myMethod");
		CtExecutableReference<?> ref = method.getReference();

		Method m = ref.getActualMethod();
		assertEquals("myMethod", m.getName());
		assertEquals(0, m.getExceptionTypes().length);
	}

	@Test
	public void testSameTypeInConstructorCallBetweenItsObjectAndItsExecutable() {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("./src/test/resources/executable/CmiContext_1.2.java");
		launcher.setSourceOutputDirectory("./target/executable");
		launcher.run();

		final CtClass<Object> aClass = launcher.getFactory().Class().get("org.objectweb.carol.jndi.spi.CmiContext");
		final List<CtConstructorCall> ctConstructorCalls = aClass.getElements(new TypeFilter<CtConstructorCall>(CtConstructorCall.class));

		for (CtConstructorCall constructorCall : ctConstructorCalls) {
			assertNotNull(constructorCall.getExecutable());
		}

		canBeBuilt("./target/executable", 8, true);
	}

	private CtAbstractInvocation<?> getInvocationFromMethod(String methodName) throws Exception {
		Factory factory = build(ExecutableRefTestSource.class, MyIntf.class);

		CtClass<ExecutableRefTestSource> clazz = factory.Class().get(ExecutableRefTestSource.class);
		Assert.assertNotNull(clazz);

		List<CtMethod<?>> methods = clazz.getMethodsByName(methodName);
		assertEquals(1, methods.size());

		CtMethod<?> ctMethod = methods.get(0);
		CtBlock<?> ctBody = (CtBlock<?>) ctMethod.getBody();
		Assert.assertNotNull(ctBody);

		List<CtStatement> ctStatements = ctBody.getStatements();
		assertEquals(1, ctStatements.size());

		CtStatement ctStatement = ctStatements.get(0);
		Assert.assertTrue(ctStatement instanceof CtAbstractInvocation<?>);

		return (CtAbstractInvocation<?>) ctStatement;
	}

	@Test
	public void testOverridingMethod() throws Exception {
		final CtType<Pozole> aPozole = ModelUtils.buildClass(Pozole.class);
		final CtExecutableReference<?> run = aPozole.getMethodsByName("run").get(0).getReference();

		final List<CtInvocation<?>> elements = Query.getElements(run.getFactory(), new InvocationFilter(run));
		assertEquals(1, elements.size());
		assertEquals(run, elements.get(0).getExecutable());
	}
}
