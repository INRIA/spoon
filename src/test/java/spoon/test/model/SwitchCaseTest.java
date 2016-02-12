package spoon.test.model;

import org.junit.Test;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.createFactory;

public class SwitchCaseTest {

	@Test
	public void testIterationStatements() {
		Factory factory = createFactory();
		CtClass<?> clazz = factory
				.Code()
				.createCodeSnippetStatement(
						"" + "class X {" + "public void foo() {" + " int x=0;" +
								"switch(x) {"
								+ "case 0: x=x+1;break;"
								+ "case 1: x=0;"
								+ "default: x=-1;"
								+ "}"
								+ "}};")
				.compile();
		CtMethod<?> foo = (CtMethod<?>) clazz.getMethods().toArray()[0];

		CtSwitch<?> sw = foo.getElements(
				new TypeFilter<CtSwitch<?>>(CtSwitch.class)).get(0);

		assertEquals(3, sw.getCases().size());

		CtCase<?> c = (CtCase<?>) sw.getCases().get(0);

		assertEquals(0, ((CtLiteral<?>) c.getCaseExpression()).getValue());
		assertEquals(2, c.getStatements().size());

		List<CtStatement> l = new ArrayList<CtStatement>();

		// this compiles (thanks to the new CtCase extends CtStatementList)
		for (CtStatement s : c) {
			l.add(s);
		}
		assertTrue(c.getStatements().equals(l));
	}

	@Test
	public void testSwitchStatementOnAString() throws Exception {
		CtClass<?> clazz = build("spoon.test.model", "SwitchStringClass");

		CtMethod<?> method = (CtMethod<?>) clazz.getMethods().toArray()[0];
		CtSwitch<?> ctSwitch = method
				.getElements(new TypeFilter<CtSwitch<?>>(CtSwitch.class))
				.get(0);

		// Checks the selector is a string.
		assertEquals(String.class,
				ctSwitch.getSelector().getType().getActualClass());

		// Checks all cases are strings.
		for (CtCase<?> aCase : ctSwitch.getCases()) {
			if (aCase.getCaseExpression() == null) {
				// default case
				continue;
			}
			assertEquals(String.class,
					aCase.getCaseExpression().getType().getActualClass());
		}
	}
}
