package spoon.test.variable;

import org.junit.Test;
import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.code.CtArrayRead;
import spoon.reflect.code.CtArrayWrite;
import spoon.reflect.code.CtExpression;
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
import spoon.test.variable.testclasses.ArrayAccessSample;
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

		assertEquals(3, variablesAccess.size());
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

		assertEquals(5, fieldsAccess.size());
	}

	@Test
	public void testCanVisitArrayAccessAndSubClasses() throws Exception {
		final Factory factory = TestUtils.build(ArrayAccessSample.class);

		final List<CtArrayRead<?>> arraysRead =
				Query.getElements(factory,
								  new AbstractFilter<CtArrayRead<?>>(CtArrayRead.class) {
									  @Override
									  public boolean matches(CtArrayRead<?> element) {
										  return super.matches(element);
									  }
								  });

		assertEquals(1, arraysRead.size());

		final List<CtArrayWrite<?>> arraysWrite =
				Query.getElements(factory,
								  new AbstractFilter<CtArrayWrite<?>>(CtArrayWrite.class) {
									  @Override
									  public boolean matches(CtArrayWrite<?> element) {
										  return super.matches(element);
									  }
								  });

		assertEquals(1, arraysWrite.size());

		final List<CtArrayAccess<?, CtExpression<?>>> arraysAccess =
				Query.getElements(factory,
								  new AbstractFilter<CtArrayAccess<?, CtExpression<?>>>(
										  CtArrayAccess.class) {
									  @Override
									  public boolean matches(
											  CtArrayAccess<?, CtExpression<?>> element) {
										  return super.matches(element);
									  }
								  });

		assertEquals(2, arraysAccess.size());
	}
}
