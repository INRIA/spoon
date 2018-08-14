package spoon.test.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.createFactory;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;

public class BlockTest {

	@Test
	public void testIterationStatements() {
		Factory factory = createFactory();
		CtClass<?> clazz = factory
				.Code()
				.createCodeSnippetStatement(
						"" + "class X {" + "public void foo() {"
								+ " int x=0;int y=0;"
								+ "}};")
				.compile();
		CtMethod<?> foo = (CtMethod<?>) clazz.getMethods().toArray()[0];

		CtBlock<?> body = foo.getBody();
		assertEquals(2, body.getStatements().size());

		List<CtStatement> l = new ArrayList<>();

		// this compiles (thanks to the new CtBlock extends CtStatementList)
		for (CtStatement s : body) {
			l.add(s);
		}

		assertTrue(body.getStatements().equals(l));
	}

	@Test
	public void testAddEmptyBlock() {
		// specifies a bug found by Benoit Cornu on August 7 2014

		Factory factory = createFactory();
		CtClass<?> clazz = factory
				.Code()
				.createCodeSnippetStatement(
						"" + "class X {" + "public void foo() {" + " "
								+ "}};")
				.compile();
		CtMethod<?> foo = (CtMethod<?>) clazz.getMethods().toArray()[0];

		CtBlock<?> body = foo.getBody(); // empty block (immutable EMPTY_LIST())

		CtCodeSnippetStatement snippet = factory.Core()
				.createCodeSnippetStatement();
		List<CtStatement> statements = body.getStatements();
		statements.add(snippet);

		assertEquals(snippet, body.getStatement(0));
		// plus implicit assertion: no exception

		CtCodeSnippetStatement snippet2 = factory.Core()
				.createCodeSnippetStatement();
		body.getStatements().add(snippet2);

		assertEquals(snippet2, body.getStatement(1));
		assertEquals(2, body.getStatements().size());

		CtCodeSnippetStatement snippet3 = factory.Core()
				.createCodeSnippetStatement();
		statements.add(snippet3);

		assertEquals(snippet3, body.getStatement(2));
		assertEquals(3, body.getStatements().size());

	}

}
