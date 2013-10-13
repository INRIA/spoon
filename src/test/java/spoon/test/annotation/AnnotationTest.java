package spoon.test.annotation;

import static org.junit.Assert.assertEquals;
import static spoon.test.TestUtils.build;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import spoon.reflect.Factory;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.builder.SpoonCompiler;
import spoon.support.builder.SpoonFile;
import spoon.support.builder.support.FileSystemFile;

public class AnnotationTest {

	@Test 
	public void testModelBuildingAnnotationBound() throws Exception {
		CtSimpleType type = build ("spoon.test.annotation",  "Bound");
		assertEquals("Bound", type.getSimpleName());
		assertEquals(1, type.getAnnotations().size());
	}

	@Test 
	public void testModelBuildingAnnotationBoundUsage() throws Exception {
		// we can not use TestUtils.build because we need to compile two classes at the same time
		SpoonCompiler comp = new SpoonCompiler();
		List<SpoonFile> files = new ArrayList();
		files.add(new FileSystemFile(new File("./src/test/java/spoon/test/annotation/Bound.java")));
		files.add(new FileSystemFile(new File("./src/test/java/spoon/test/annotation/Main.java")));
		Factory factory = new Factory(new DefaultCoreFactory(), new StandardEnvironment());
		comp.compileSrc(factory, files);
		CtSimpleType type =  factory.Package().get("spoon.test.annotation").getType("Main");				
		
		assertEquals("Main", type.getSimpleName());
		CtParameter param = (CtParameter) type.getElements(new TypeFilter(CtParameter.class)).get(0);
		assertEquals("a", param.getSimpleName());
		Set<CtAnnotation<? extends Annotation>> annotations = param.getAnnotations();
		CtAnnotation a = annotations.toArray(new CtAnnotation[0])[0];
		assertEquals(1, annotations.size());
		Bound actualAnnotation = (Bound)a.getActualAnnotation();
		assertEquals(8, actualAnnotation.max());
	}

}
