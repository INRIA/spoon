package spoon.test.replace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import spoon.reflect.Factory;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.builder.SpoonCompiler;
import spoon.support.builder.SpoonFile;
import spoon.support.builder.support.FileSystemFile;

public class ReplaceTest {

	Factory factory;

	@Before
	public void setup() throws Exception {
		factory = new Factory(new DefaultCoreFactory(), new StandardEnvironment());
		SpoonCompiler comp = new SpoonCompiler();
		List<SpoonFile> files = new ArrayList();
		SpoonFile file = new FileSystemFile(new File("./src/test/java/spoon/test/replace/Foo.java"));
		files.add(file);
		comp.compileSrc(factory, files);		
	}
	@Test 
	public void testReplaceSet() throws Exception {		
		
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

	@Test 
	public void testReplaceBlock() throws Exception {		
		CtClass foo = (CtClass) factory.Package().get("spoon.test.replace").getType("Foo");
		CtMethod m = foo.getElements(new TypeFilter<CtMethod>(CtMethod.class)).get(0);
		assertEquals("foo", m.getSimpleName());
		
		CtAssignment assignment = (CtAssignment)m.getBody().getStatements().get(2);

		CtExpression s1 = assignment.getAssignment();		
		CtExpression s2 = factory.Code().createLiteral(3);
		
		assertEquals("z = x + 1",assignment.toString());
		assertEquals("x + 1",s1.toString());

		// do
		s1.replace(s2);		
		assertSame(s2,assignment.getAssignment());
		assertEquals("z = 3",assignment.toString());

		// undo
		s2.replace(s1);		
		assertSame(s1,assignment.getAssignment());
		assertEquals("z = x + 1",assignment.toString());
	}

	
}
