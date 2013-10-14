package spoon.test.initializers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static spoon.test.TestUtils.build;

import org.junit.Test;

import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;

public class InitializerTest {

	@Test 
	public void testModelBuildingInitializer() throws Exception {
		CtClass type = (CtClass)build ("spoon.test.initializers",  "InstanceInitializers");
		assertEquals("InstanceInitializers", type.getSimpleName());
		
		CtField k = (CtField) type.getElements(new NameFilter("k")).get(0);
		assertTrue(k.getDefaultExpression() instanceof CtNewClass);
		
		CtField l = (CtField) type.getElements(new NameFilter("l")).get(0);
		assertTrue(l.getDefaultExpression() instanceof CtNewClass);
		
		CtField x = (CtField) type.getElements(new NameFilter("x")).get(0);
		assertTrue(x.getDefaultExpression() == null);

		CtField y = (CtField) type.getElements(new NameFilter("y")).get(0);
		assertTrue(y.getDefaultExpression() instanceof CtLiteral);

		CtField z = (CtField) type.getElements(new NameFilter("z")).get(0);
		assertTrue(z.getDefaultExpression().toString().equals("5"));
		
		// static initializer
		CtAnonymousExecutable ex = type.getElements(new TypeFilter<CtAnonymousExecutable>(CtAnonymousExecutable.class)).get(0);
		assertEquals("x = 3", ex.getBody().getStatements().get(0).toString());
		
	}
}
