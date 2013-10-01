package spoon.test.replace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import spoon.reflect.Factory;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.builder.SpoonCompiler;
import spoon.support.builder.SpoonFile;
import spoon.support.builder.support.FileSystemFile;

public class ReplaceTest {

	@Test 
	public void testReplace() throws Exception {		
		SpoonCompiler comp = new SpoonCompiler();
		List<SpoonFile> files = new ArrayList();
		SpoonFile file = new FileSystemFile(new File("./src/test/java/spoon/test/replace/Foo.java"));
		files.add(file);
		Factory factory = new Factory(new DefaultCoreFactory(), new StandardEnvironment());
		comp.compileSrc(factory, files);
		
		CtClass foo = (CtClass) factory.Package().get("spoon.test.replace").getType("Foo");
		assertEquals("Foo", foo.getSimpleName());
		CtClass bar = (CtClass) factory.Package().get("spoon.test.replace").getType("Bar");
		assertEquals("Bar", bar.getSimpleName());
		
		CtField i1 = foo.getField("i");
		CtField i2 = bar.getField("i");
		
		assertEquals("int",foo.getField("i").getType().getSimpleName());

		// do
		i1.replace(i2);		
		assertSame(i2,foo.getField("i"));
		assertEquals("float",foo.getField("i").getType().getSimpleName());

		// undo
		i2.replace(i1);		
		assertSame(i1,foo.getField("i"));
		assertEquals("int",foo.getField("i").getType().getSimpleName());		
	}

	
}
