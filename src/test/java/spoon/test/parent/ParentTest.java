package spoon.test.parent;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import spoon.Launcher;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.NameFilter;

public class ParentTest {
    
	Factory factory;

	@Before
	public void setup() throws Exception {
		Launcher spoon = new Launcher();
		factory = spoon.createFactory();
		spoon.createCompiler(
				factory,
				SpoonResourceHelper
						.resources("./src/test/java/spoon/test/parent/Foo.java"))
				.build();
	}

	@Test
	public void testParent() throws Exception {
		// toString should not throw a parent exception even if parents are not
		// set
		try {
			CtLiteral<Object> literal = factory.Core().createLiteral();
			literal.setValue(1);
			CtBinaryOperator<?> minus = factory.Core().createBinaryOperator();
			minus.setKind(BinaryOperatorKind.MINUS);
			minus.setRightHandOperand(literal);
			minus.setLeftHandOperand(literal);
		} catch (Exception e) {
			Assert.fail();
		}
	}
 
	@Test
	public void testParentSet() throws Exception {
		CtClass<?> foo = factory.Package().get("spoon.test.parent")
				.getType("Foo");
		
		CtMethod<?> fooMethod = foo.getElements(
				new NameFilter<CtMethod<?>>("foo")).get(0);
		assertEquals("foo", fooMethod.getSimpleName());

		CtLocalVariable<?> localVar = (CtLocalVariable<?>) fooMethod.getBody()
				.getStatements().get(0);

		CtAssignment<?,?> assignment = (CtAssignment<?,?>) fooMethod.getBody()
				.getStatements().get(1);


		CtLiteral<?> newLit = factory.Code().createLiteral(0);
		localVar.setDefaultExpression((CtExpression) newLit);
		assertEquals(localVar, newLit.getParent());
		
		CtLiteral<?> newLit2 = factory.Code().createLiteral(1);
		assignment.setAssignment((CtExpression) newLit2);
		assertEquals(assignment, newLit2.getParent());
				
	}
}
