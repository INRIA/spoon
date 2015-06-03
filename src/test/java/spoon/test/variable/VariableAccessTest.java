package spoon.test.variable;

import org.junit.Test;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.test.TestUtils;
import spoon.test.variable.testclasses.VariableAccessSample;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class VariableAccessTest {
	@Test
	public void testCanVisitVariableAccessAndSubClasses() throws Exception {
		final Factory factory = TestUtils.build(VariableAccessSample.class);

		final List<CtVariableRead<?>> variableReads =
				Query.getElements(factory,
								  new AbstractFilter<CtVariableRead<?>>(CtVariableRead.class) {
									  @Override
									  public boolean matches(CtVariableRead<?> element) {
										  return super.matches(element);
									  }
								  });

		assertEquals(2, variableReads.size());

		final List<CtVariableWrite<?>> variableWrites =
				Query.getElements(factory,
								  new AbstractFilter<CtVariableWrite<?>>(CtVariableWrite.class) {
									  @Override
									  public boolean matches(CtVariableWrite<?> element) {
										  return super.matches(element);
									  }
								  });

		assertEquals(1, variableWrites.size());

		final List<CtVariableAccess<?>> variableAccess =
				Query.getElements(factory,
								  new AbstractFilter<CtVariableAccess<?>>(CtVariableAccess.class) {
									  @Override
									  public boolean matches(CtVariableAccess<?> element) {
										  return super.matches(element);
									  }
								  });

		assertEquals(variableReads.size() + variableWrites.size(), variableAccess.size());
	}
}
