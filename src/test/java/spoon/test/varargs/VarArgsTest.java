package spoon.test.varargs;

import static org.junit.Assert.assertEquals;
import static spoon.test.TestUtils.build;

import org.junit.Test;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.test.trycatch.Main;

public class VarArgsTest {

	@Test
	public void testModelBuildingInitializer() throws Exception {
		CtClass<Main> type = build("spoon.test.varargs", "VarArgsSample");
		assertEquals("VarArgsSample", type.getSimpleName());
		CtMethod<?> m = type.getMethodsByName("foo").get(0);

		CtParameter<?> param0 = m.getParameters().get(0);
		assertEquals(false, param0.isVarArgs());

		CtParameter<?> param1 = m.getParameters().get(1);
		assertEquals(true, param1.isVarArgs());
		assertEquals("java.lang.String[]", param1.getType().toString());
		assertEquals("Array", param1.getType().getSimpleName());
		assertEquals("java.lang.String", ((CtArrayTypeReference<?>)param1.getType()).getComponentType().toString());
		// we can even rewrite the vararg
		assertEquals("void foo(int arg0, java.lang.String... args) {"
				+ DefaultJavaPrettyPrinter.LINE_SEPARATOR + "}", m.toString());
	}


}
