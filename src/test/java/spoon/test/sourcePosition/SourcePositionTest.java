package spoon.test.sourcePosition;

import org.junit.Test;

import spoon.reflect.code.CtInvocation;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.sourcePosition.testclasses.Brambora;
import spoon.testing.utils.ModelUtils;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.testing.utils.ModelUtils.build;

public class SourcePositionTest {

	@Test
	public void equalPositionsHaveSameHashcode() throws Exception {
		String packageName = "spoon.test.testclasses";
		String sampleClassName = "SampleClass";
		String qualifiedName = packageName + "." + sampleClassName;

		Filter<CtMethod<?>> methodFilter = new TypeFilter<CtMethod<?>>(CtMethod.class);

		Factory aFactory = factoryFor(packageName, sampleClassName);
		List<CtMethod<?>> methods = aFactory.Class().get(qualifiedName).getElements(methodFilter);

		Factory newInstanceOfSameFactory = factoryFor(packageName, sampleClassName);
		List<CtMethod<?>> newInstanceOfSameMethods = newInstanceOfSameFactory.Class().get(qualifiedName).getElements(methodFilter);

		assertEquals(methods.size(), newInstanceOfSameMethods.size());
		for (int i = 0; i < methods.size(); i += 1) {
			SourcePosition aPosition = methods.get(i).getPosition();
			SourcePosition newInstanceOfSamePosition = newInstanceOfSameMethods.get(i).getPosition();
			assertTrue(aPosition.equals(newInstanceOfSamePosition));
			assertEquals(aPosition.hashCode(), newInstanceOfSamePosition.hashCode());
		}
	}

	private Factory factoryFor(String packageName, String className) throws Exception {
		return build(packageName, className).getFactory();
	}
	
	@Test
	public void testSourcePositionOfSecondPrimitiveType() throws Exception {
		/*
		 * contract: fix bug: the other references to primitive type (e.g. void)
		 * in return type of ExecutableRefernce "System.out.println" DOES NOT copy the source position
		 * from the return type of owner method
		 */
		CtType<?> type = ModelUtils.buildClass(Brambora.class);
		CtInvocation<?> invocation = type.getMethodsByName("sourcePositionOfMyReturnTypeMustNotBeCopied").get(0).getBody().getStatement(0);
		CtExecutableReference<?> execRef = invocation.getExecutable();
		CtTypeReference<?> typeOfReturnValueOfPrintln = execRef.getType();
		assertEquals("void", typeOfReturnValueOfPrintln.getQualifiedName());
		SourcePosition sp = typeOfReturnValueOfPrintln.getPosition();
		if (sp != null && sp instanceof NoSourcePosition == false) {
			//it copied source position from owner method return type
			fail("The source position of invisible implicit reference to void is: [" + sp.getSourceStart() + "; " + sp.getSourceEnd() + "]");
		}
	}

}