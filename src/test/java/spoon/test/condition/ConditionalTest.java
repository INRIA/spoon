package spoon.test.condition;

import org.junit.Test;
import spoon.reflect.code.CtConditional;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.condition.testclasses.Foo;
import spoon.testing.utils.ModelUtils;

import static org.junit.Assert.assertEquals;

public class ConditionalTest {
	@Test
	public void testConditional() throws Exception {
		final CtType<Foo> aFoo = ModelUtils.buildClass(Foo.class);
		final CtConditional aConditional = aFoo.getMethod("m2").getElements(new TypeFilter<CtConditional>(CtConditional.class)).get(0);
		assertEquals("return a == 18 ? true : false", aConditional.getParent().toString());
	}

	@Test
	public void testConditionalWithAssignment() throws Exception {
		final CtType<Foo> aFoo = ModelUtils.buildClass(Foo.class);
		final CtConditional aConditional = aFoo.getMethod("m").getElements(new TypeFilter<CtConditional>(CtConditional.class)).get(0);
		assertEquals("x = (a == 18) ? true : false", aConditional.getParent().toString());
	}
}
