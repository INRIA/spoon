package spoon.test.annotation;

import org.junit.Test;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.annotation.testclasses.Pozole;
import spoon.testing.utils.ModelUtils;

import static org.junit.Assert.assertEquals;

public class AnnotationLoopTest {
	@Test
	public void testAnnotationDeclaredInForInit() throws Exception {
		final CtType<Pozole> aPozole = ModelUtils.buildClass(Pozole.class);

		final CtFor aLoop = aPozole.getMethod("cook").getElements(new TypeFilter<>(CtFor.class)).get(0);
		assertEquals(3, aLoop.getForInit().size());
		assertEquals(SuppressWarnings.class, aLoop.getForInit().get(0).getAnnotations().get(0).getAnnotationType().getActualClass());
		assertEquals(SuppressWarnings.class, aLoop.getForInit().get(1).getAnnotations().get(0).getAnnotationType().getActualClass());
		assertEquals(SuppressWarnings.class, aLoop.getForInit().get(2).getAnnotations().get(0).getAnnotationType().getActualClass());

		assertEquals("u", ((CtLocalVariable) aLoop.getForInit().get(0)).getSimpleName());
		assertEquals("p", ((CtLocalVariable) aLoop.getForInit().get(1)).getSimpleName());
		assertEquals("e", ((CtLocalVariable) aLoop.getForInit().get(2)).getSimpleName());

		assertEquals(aPozole.getFactory().Type().STRING, ((CtLocalVariable) aLoop.getForInit().get(0)).getType());
		assertEquals(aPozole.getFactory().Type().STRING, ((CtLocalVariable) aLoop.getForInit().get(1)).getType());
		assertEquals(aPozole.getFactory().Type().STRING, ((CtLocalVariable) aLoop.getForInit().get(2)).getType());

		final String nl = System.lineSeparator();
		final String expected = "for (@java.lang.SuppressWarnings(value = \"rawtypes\")" + nl + "java.lang.String u = \"\", p = \"\", e = \"\"; u != e; u = p , p = \"\") {" + nl + "}";
		assertEquals(expected, aLoop.toString());
	}
}
