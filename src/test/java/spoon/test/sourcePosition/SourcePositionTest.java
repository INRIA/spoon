package spoon.test.sourcePosition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.TestUtils;

public class SourcePositionTest {

	@Test
	public void equalPositionsHaveSameHashcode() throws Exception {
		String packageName = "spoon.test";
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
		return TestUtils.build(packageName, className).getFactory();
	}

}