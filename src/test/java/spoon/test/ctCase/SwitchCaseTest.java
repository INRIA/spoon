package spoon.test.ctCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.TestUtils;

@SuppressWarnings({"unchecked", "rawtypes"})
public class SwitchCaseTest {

	@Test
	public void insertAfterStatementInSwitchCaseWithoutException() throws Exception {
		String packageName = "spoon.test.ctCase";
		String className = "ClassWithSwitchExample";
		Factory factory = factoryFor(packageName, className);
		List<CtCase> elements = elementsOfType(CtCase.class, factory);
		assertEquals(3, elements.size());
		CtCase firstCase = elements.get(0);
		List<CtStatement> statements = firstCase.getStatements();
		assertEquals(2, statements.size());
		CtStatement newStatement = factory.Code().createCodeSnippetStatement("result = 0");
		statements.get(0).insertAfter(newStatement);
		statements = firstCase.getStatements();
		assertEquals(3, statements.size());
		assertTrue(statements.get(1) == newStatement);
	}
	
	@Test
	public void insertBeforeStatementInSwitchCaseWithoutException() throws Exception {
		String packageName = "spoon.test.ctCase";
		String className = "ClassWithSwitchExample";
		Factory factory = factoryFor(packageName, className);
		List<CtCase> elements = elementsOfType(CtCase.class, factory);
		assertEquals(3, elements.size());
		CtCase firstCase = elements.get(0);
		List<CtStatement> statements = firstCase.getStatements();
		assertEquals(2, statements.size());
		CtStatement newStatement = factory.Code().createCodeSnippetStatement("result = 0");
		statements.get(0).insertBefore(newStatement);
		statements = firstCase.getStatements();
		assertEquals(3, statements.size());
		assertTrue(statements.get(0) == newStatement);
	}
	
	private <T extends CtElement> List<T> elementsOfType(Class<T> type, Factory factory) {
		return (List) Query.getElements(factory, new TypeFilter<>(type));
	}
	
	private Factory factoryFor(String packageName, String className) throws Exception {
		return TestUtils.build(packageName, className).getFactory();
	}
}
