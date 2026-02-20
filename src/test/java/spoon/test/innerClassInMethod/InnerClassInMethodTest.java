package spoon.test.innerClassInMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static spoon.testing.assertions.SpoonAssertions.assertThat;

import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.testing.utils.ModelTest;

public class InnerClassInMethodTest {

	@ModelTest(value = "./src/test/resources/innerClassInMethod/InnerClassInMethod.java")
	void testAnonymity(Factory factory) {
		// contract: the anonymity status of inner classes declared in methods is preserved
		CtModel model = factory.getModel();
		CtMethod<?> method = model.getElements(new TypeFilter<>(CtMethod.class)).get(0);
		assertThat(method).isNotNull();
		CtClass<?> anonymousClass = method.getElements(new TypeFilter<>(CtClass.class)).get(0);
		assertThat(anonymousClass).isNotNull();
		assertTrue(anonymousClass.isAnonymous());
		CtClass<?> notAnonymousClass = method.getElements(new TypeFilter<>(CtClass.class)).get(1);
		assertThat(notAnonymousClass).isNotNull();
		assertFalse(notAnonymousClass.isAnonymous());
		assertThat(notAnonymousClass).getSimpleName().isEqualTo("NotAnonymousClass$1");
		String expected =
			"void m() {\n" +
			"    new java.lang.Runnable() {\n" +
			"        public void run() {\n" +
			"        }\n" +
			"    };\n" +
			"    class NotAnonymousClass$1 {}\n" +
			"}";
		assertEquals(expected, method.toString());
	}
}
