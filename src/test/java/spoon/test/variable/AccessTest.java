package spoon.test.variable;

import org.junit.Test;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.test.TestUtils;
import spoon.test.variable.testclasses.FieldAccessSample;
import spoon.test.variable.testclasses.VariableAccessSample;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class AccessTest {
	@Test
	public void testCanVisitVariableAccessAndSubClasses() throws Exception {
		final Factory factory = TestUtils.build(VariableAccessSample.class);

		final List<CtVariableRead<?>> variablesRead =
				Query.getElements(factory,
								  new AbstractFilter<CtVariableRead<?>>(CtVariableRead.class) {
									  @Override
									  public boolean matches(CtVariableRead<?> element) {
										  return super.matches(element);
									  }
								  });

		assertEquals(2, variablesRead.size());

		final List<CtVariableWrite<?>> variablesWrite =
				Query.getElements(factory,
								  new AbstractFilter<CtVariableWrite<?>>(CtVariableWrite.class) {
									  @Override
									  public boolean matches(CtVariableWrite<?> element) {
										  return super.matches(element);
									  }
								  });

		assertEquals(1, variablesWrite.size());

		final List<CtVariableAccess<?>> variablesAccess =
				Query.getElements(factory,
								  new AbstractFilter<CtVariableAccess<?>>(CtVariableAccess.class) {
									  @Override
									  public boolean matches(CtVariableAccess<?> element) {
										  return super.matches(element);
									  }
								  });

		assertEquals(variablesRead.size() + variablesWrite.size(), variablesAccess.size());
	}

	@Test
	public void testCanVisitFieldAccessAndSubClasses() throws Exception {
		final Factory factory = TestUtils.build(FieldAccessSample.class);

		final List<CtFieldRead<?>> fieldsRead =
				Query.getElements(factory,
								  new AbstractFilter<CtFieldRead<?>>(CtFieldRead.class) {
									  @Override
									  public boolean matches(CtFieldRead<?> element) {
										  return super.matches(element);
									  }
								  });

		assertEquals(2, fieldsRead.size());

		final List<CtFieldWrite<?>> fieldsWrite =
				Query.getElements(factory,
								  new AbstractFilter<CtFieldWrite<?>>(CtFieldWrite.class) {
									  @Override
									  public boolean matches(CtFieldWrite<?> element) {
										  return super.matches(element);
									  }
								  });

		assertEquals(3, fieldsWrite.size());

		final List<CtFieldAccess<?>> fieldsAccess =
				Query.getElements(factory,
								  new AbstractFilter<CtFieldAccess<?>>(CtFieldAccess.class) {
									  @Override
									  public boolean matches(CtFieldAccess<?> element) {
										  return super.matches(element);
									  }
								  });

		assertEquals(fieldsRead.size() + fieldsWrite.size(), fieldsAccess.size());
	}
}
