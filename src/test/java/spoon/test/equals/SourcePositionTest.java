package spoon.test.equals;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.TestUtils;

public class SourcePositionTest {

	@Before
	public void initialize() throws Exception {
		packageName = "spoon.test";
		targetClassName = "SampleClass";
		factoryA = TestUtils.build("spoon.test", targetClassName).getFactory();
		factoryB = TestUtils.build("spoon.test", targetClassName).getFactory();
	}
	
	@Test
	public void testEquals() throws Exception {
		Filter<CtMethod<?>> methodFilter = new TypeFilter<CtMethod<?>>(CtMethod.class);
		List<CtMethod<?>> methodsA = factoryA.Class().get(qualifiedName()).getElements(methodFilter);
		List<CtMethod<?>> methodsB = factoryB.Class().get(qualifiedName()).getElements(methodFilter);
		for (int i = 0; i < methodsA.size(); i += 1) {
			assertTrue(methodsA.get(i).getPosition().equals(methodsB.get(i).getPosition()));
		}
	}
	
	@Test
	public void testHashCode() throws Exception {
		Filter<CtMethod<?>> methodFilter = new TypeFilter<CtMethod<?>>(CtMethod.class);
		List<CtMethod<?>> methodsA = factoryA.Class().get(qualifiedName()).getElements(methodFilter);
		List<CtMethod<?>> methodsB = factoryB.Class().get(qualifiedName()).getElements(methodFilter);
		for (int i = 0; i < methodsA.size(); i += 1) {
			assertTrue(methodsA.get(i).getPosition().hashCode()  == methodsB.get(i).getPosition().hashCode());
		}
	}
	
	private String qualifiedName() {
		return packageName + "." + targetClassName;
	}
	
	String packageName;
	String targetClassName;
	Factory factoryA;
	Factory factoryB;
}
