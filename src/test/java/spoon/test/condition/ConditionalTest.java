package spoon.test.condition;

import org.junit.Test;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtIf;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.condition.testclasses.Foo;
import spoon.testing.utils.ModelUtils;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

	@Test
	public void testBlockInConditionAndLoop() throws Exception {
		final CtType<Foo> aFoo = ModelUtils.buildClass(Foo.class);
		final List<CtIf> conditions = aFoo.getMethod("m3").getElements(new TypeFilter<CtIf>(CtIf.class));
		assertEquals(4, conditions.size());
		for (CtIf condition : conditions) {
			assertTrue(condition.getThenStatement() instanceof CtBlock);
			if (condition.getElseStatement() != null && !(condition.getElseStatement() instanceof CtIf)) {
				assertTrue(condition.getElseStatement() instanceof CtBlock);
			}
		}
	}
}
