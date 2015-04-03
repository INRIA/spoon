package spoon.test.executable;

import org.junit.Assert;
import org.junit.Test;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.test.TestUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

public class ExecutableRefTest {

	@Test
	public void methodTest() throws Exception {
		CtAbstractInvocation<?> ctAbstractInvocation = this.getInvocationFromMethod("testMethod");
		Assert.assertTrue(ctAbstractInvocation instanceof CtInvocation<?>);

		CtExecutableReference<?> executableReference = ctAbstractInvocation.getExecutable();
		Assert.assertNotNull(executableReference);

		Method method = executableReference.getActualMethod();
		Assert.assertNotNull(method);

		Assert.assertEquals("Hello World",
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

		Assert.assertEquals("Hello World",
				constructor.newInstance(((CtLiteral<?>) ctAbstractInvocation.getArguments().get(0)).getValue()));
	}

	@Test
	public void testGetActualClassTest() throws Exception {
		Factory factory = TestUtils.build(ExecutableRefTestSource.class, MyIntf.class);

		CtMethod<?> method = factory.Class().get(ExecutableRefTestSource.class).getMethod("myMethod");
		CtExecutableReference<?> ref = method.getReference();

		Method m = ref.getActualMethod();
		Assert.assertEquals("myMethod", m.getName());
		Assert.assertEquals(0, m.getExceptionTypes().length);
	}

	private CtAbstractInvocation<?> getInvocationFromMethod(String methodName) throws Exception {
		Factory factory = TestUtils.build(ExecutableRefTestSource.class, MyIntf.class);

		CtClass<ExecutableRefTestSource> clazz = factory.Class().get(ExecutableRefTestSource.class);
		Assert.assertNotNull(clazz);

		List<CtMethod<?>> methods = clazz.getMethodsByName(methodName);
		Assert.assertEquals(1, methods.size());

		CtMethod<?> ctMethod = methods.get(0);
		CtBlock<?> ctBody = (CtBlock<?>) ctMethod.getBody();
		Assert.assertNotNull(ctBody);

		List<CtStatement> ctStatements = ctBody.getStatements();
		Assert.assertEquals(1, ctStatements.size());

		CtStatement ctStatement = ctStatements.get(0);
		Assert.assertTrue(ctStatement instanceof CtAbstractInvocation<?>);

		return (CtAbstractInvocation<?>) ctStatement;
	}
}
