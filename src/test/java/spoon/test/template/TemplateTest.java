package spoon.test.template;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import spoon.reflect.Factory;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.builder.SpoonCompiler;
import spoon.support.builder.SpoonFile;
import spoon.support.builder.support.FileSystemFile;
import spoon.template.Substitution;

public class TemplateTest {

	@Test
	public void testTemplateInheritance() throws Exception {
		SpoonCompiler comp = new SpoonCompiler();
		List<SpoonFile> files = new ArrayList();
		files.add(new FileSystemFile(new File(
				"./src/test/java/spoon/test/template/SubClass.java")));
		files.add(new FileSystemFile(new File(
				"./src/test/java/spoon/test/template/SuperClass.java")));
		List<SpoonFile> templates = new ArrayList();
		templates.add(new FileSystemFile(new File(
				"./src/test/java/spoon/test/template/SubTemplate.java")));
		templates.add(new FileSystemFile(new File(
				"./src/test/java/spoon/test/template/SuperTemplate.java")));
		Factory factory = new Factory(new DefaultCoreFactory(),
				new StandardEnvironment());
		comp.compileSrc(factory, files);
		comp.compileTemplate(factory, templates);

		CtClass<?> superc = factory.Class().get(SuperClass.class);
		Substitution.insertAll(superc, new SuperTemplate());

		CtMethod addedMethod = (CtMethod) superc.getElements(
				new NameFilter("toBeOverriden")).get(0);
		assertEquals("toBeOverriden",
				addedMethod.getSimpleName());

		CtClass<?> subc = factory.Class().get(SubClass.class);
		Substitution.insertAll(subc, new SubTemplate());
		CtMethod addedMethod2 = (CtMethod) subc.getElements(
				new NameFilter("toBeOverriden")).get(0);
		assertEquals("toBeOverriden",
				addedMethod2.getSimpleName());
		assertEquals("super.toBeOverriden()",
				addedMethod2.getBody().getStatements().get(0).toString());

	}

	@Test
	public void testTemplateC1() throws Exception {
		SpoonCompiler comp = new SpoonCompiler();
		List<SpoonFile> files = new ArrayList();
		files.add(new FileSystemFile(new File(
				"./src/test/java/spoon/test/template/C1.java")));
		List<SpoonFile> templates = new ArrayList();
		templates
				.add(new FileSystemFile(
						new File(
								"./src/test/java/spoon/test/template/TemplateWithConstructor.java")));
		Factory factory = new Factory(new DefaultCoreFactory(),
				new StandardEnvironment());
		comp.compileSrc(factory, files);
		comp.compileTemplate(factory, templates);

		CtClass<?> c1 = factory.Class().get(C1.class);

		// before template: 1 constructor
		assertEquals(1, // this is the default implicit constructor
				c1.getConstructors().size());

		// the actual substitution
		Substitution.insertAll(c1, new TemplateWithConstructor(factory.Type()
				.createReference(Date.class)));

		// after template: 3 constructors
		assertEquals(3,
				c1.getConstructors().size());

		CtField toBeInserted = (CtField) c1.getElements(
				new NameFilter("toBeInserted")).get(0);
		assertEquals(Date.class,
				toBeInserted.getType().getActualTypeArguments().get(0)
						.getActualClass());
		assertEquals(
				"java.util.List<java.util.Date>  toBeInserted = new java.util.ArrayList<java.util.Date> ();",
				toBeInserted.toString());

	}

}
