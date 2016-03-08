package spoon.test.loop;

import org.junit.Test;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.loop.testclasses.Foo;

import static org.junit.Assert.assertEquals;
import static spoon.testing.utils.ModelUtils.buildClass;

public class LoopTest {
	@Test
	public void testAnnotationInForLoop() throws Exception {
		CtType<Foo> aFoo = buildClass(Foo.class);

		CtFor aFor = aFoo.getMethod("m").getElements(new TypeFilter<>(CtFor.class)).get(0);
		assertEquals(1, ((CtLocalVariable) aFor.getForInit().get(0)).getType().getAnnotations().size());
		assertEquals(1, ((CtLocalVariable) aFor.getForInit().get(1)).getType().getAnnotations().size());

		CtForEach aForEach = aFoo.getMethod("m").getElements(new TypeFilter<>(CtForEach.class)).get(0);
		assertEquals(1, aForEach.getVariable().getType().getAnnotations().size());
	}
}
