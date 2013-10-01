package spoon.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import spoon.reflect.Factory;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.builder.SpoonCompiler;
import spoon.support.builder.SpoonFile;
import spoon.support.builder.support.FileSystemFile;

public class Tests {

	private CtSimpleType build(String packageName, String className) throws Exception {
		SpoonCompiler comp = new SpoonCompiler();
		List<SpoonFile> files = new ArrayList();
		SpoonFile file = new FileSystemFile(new File("./src/test/java/"+packageName.replace('.', '/')+"/"+className+".java"));
		files.add(file);
		Factory factory = new Factory(new DefaultCoreFactory(), new StandardEnvironment());
		comp.compileSrc(factory, files);
		return factory.Package().get(packageName).getType(className);				
	}
	
	@Test 
	public void testModelBuildingBound() throws Exception {
		CtSimpleType type = build ("spoon.test.annotation",  "Bound");
		assertEquals("Bound", type.getSimpleName());
		assertEquals(1, type.getAnnotations().size());
	}

	@Test 
	public void testModelBuildingArrays() throws Exception {
		CtSimpleType type = build ("spoon.test.arrays",  "ArrayTests");
		assertEquals("ArrayTests", type.getSimpleName());
		assertEquals("int[][][]", type.getField("i").getType().toString());
	}

	@Test 
	public void testModelBuildingTree() throws Exception {
		CtClass type = (CtClass)build ("spoon.test.generics",  "Tree");
		assertEquals("Tree", type.getSimpleName());
		CtTypeParameterReference generic = (CtTypeParameterReference) type.getFormalTypeParameters().get(0);
		assertEquals("V", generic.getSimpleName());
		assertEquals("[java.io.Serializable, java.lang.Comparable]", generic.getBounds().toString());
	}

}
