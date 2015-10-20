package spoon.test.constructor;

import org.junit.Before;
import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.constructor.testclasses.AClass;
import spoon.test.constructor.testclasses.Tacos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static spoon.test.TestUtils.canBeBuild;

public class ConstructorTest {
	private Factory factory;
	private CtClass<?> aClass;

	@Before
	public void setUp() throws Exception {
		SpoonAPI launcher = new Launcher();
		launcher.run(new String[] {
				"-i", "./src/test/java/spoon/test/constructor/testclasses/",
				"-o", "./target/spooned/"
		});
		factory = launcher.getFactory();
		aClass = factory.Class().get(Tacos.class);
	}

	@Test
	public void testTransformationOnConstructorWithInsertBegin() throws Exception {
		final CtConstructor<?> ctConstructor = aClass.getElements(new TypeFilter<CtConstructor<?>>(CtConstructor.class)).get(0);
		ctConstructor.getBody().insertBegin(factory.Code().createCodeSnippetStatement("int i = 0"));

		assertEquals(2, ctConstructor.getBody().getStatements().size());
		assertEquals("super()", ctConstructor.getBody().getStatement(0).toString());

		canBeBuild("./target/spooned/spoon/test/constructor/testclasses/", 8);
	}

	@Test
	public void testTransformationOnConstructorWithInsertBefore() throws Exception {
		final CtConstructor<?> ctConstructor = aClass.getElements(new TypeFilter<CtConstructor<?>>(CtConstructor.class)).get(0);
		try {
			ctConstructor.getBody().getStatement(0).insertBefore(factory.Code().createCodeSnippetStatement("int i = 0"));
			fail();
		} catch (RuntimeException ignore) {
		}
		assertEquals(1, ctConstructor.getBody().getStatements().size());
		assertEquals("super()", ctConstructor.getBody().getStatement(0).toString());
	}

	@Test
	public void callParamConstructor() throws Exception {
		CtClass<Object> aClass = factory.Class().get(AClass.class);
		CtConstructor<Object> constructor = aClass.getConstructors().iterator().next();
		assertEquals("{" + System.lineSeparator() +
				"    enclosingInstance.super();" + System.lineSeparator()
				+ "}", constructor.getBody().toString());
	}
}
