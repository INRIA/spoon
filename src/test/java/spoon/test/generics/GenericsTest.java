package spoon.test.generics;

import static org.junit.Assert.assertEquals;
import static spoon.test.TestUtils.build;

import java.util.List;

import org.junit.Test;

import spoon.reflect.Factory;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;

public class GenericsTest {

	@Test 
	public void testModelBuildingTree() throws Exception {
		CtClass type = (CtClass)build ("spoon.test.generics",  "Tree");
		assertEquals("Tree", type.getSimpleName());
		CtTypeParameterReference generic = (CtTypeParameterReference) type.getFormalTypeParameters().get(0);
		assertEquals("V", generic.getSimpleName());
		assertEquals("[java.io.Serializable, java.lang.Comparable]", generic.getBounds().toString());
	}
	
	@Test 
	public void testModelBuildingGenericConstructor() throws Exception {
		CtClass type = (CtClass)build ("spoon.test.generics",  "GenericConstructor");
		assertEquals("GenericConstructor", type.getSimpleName());
		CtTypeParameterReference generic = (CtTypeParameterReference) type.getElements(new TypeFilter<CtConstructor>(CtConstructor.class)).get(0).getFormalTypeParameters().get(0);
		assertEquals("E", generic.getSimpleName());
	}

	@Test 
	public void testModelBuildingSimilarSignatureMethodes() throws Exception {
		CtClass type = (CtClass)build ("spoon.test.generics",  "SimilarSignatureMethodes");
		List<CtNamedElement> methods = type.getElements(new NameFilter("methode"));
		assertEquals(2, methods.size());
		CtTypeParameterReference generic = (CtTypeParameterReference) ((CtMethod)methods.get(0)).getFormalTypeParameters().get(0);
		assertEquals("E", generic.getSimpleName());
		CtParameter param = (CtParameter) ((CtMethod)methods.get(0)).getParameters().get(0);
		assertEquals("E", param.getType().toString());
	}
	
	@Test
	public void testDiamond() {
		Factory factory = new Factory(new DefaultCoreFactory(), new StandardEnvironment());		
		CtClass clazz = (CtClass) factory.Code().createCodeSnippetStatement(
				"class Diamond {\n" + 
				"	java.util.List<String> f = new java.util.ArrayList<>();\n" + 
				"}"
		).compile();
		CtField f = (CtField) clazz.getFields().toArray()[0];
		CtNewClass val = (CtNewClass)f.getDefaultExpression();
		
		// the diamond is resolved to String 
		assertEquals("java.lang.String", val.getType().getActualTypeArguments().get(0).toString());
		assertEquals("new java.util.ArrayList<java.lang.String>()", val.toString());		
	}
	
}
