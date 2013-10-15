package spoon.test.template;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import spoon.reflect.Factory;
import spoon.reflect.declaration.CtClass;
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
	public void testTemplate() throws Exception {
		SpoonCompiler comp = new SpoonCompiler();
		List<SpoonFile> files = new ArrayList();
		files.add(new FileSystemFile(new File("./src/test/java/spoon/test/template/SubClass.java")));
		files.add(new FileSystemFile(new File("./src/test/java/spoon/test/template/SuperClass.java")));
		List<SpoonFile> templates = new ArrayList();
		templates.add(new FileSystemFile(new File("./src/test/java/spoon/test/template/SubTemplate.java")));
		templates.add(new FileSystemFile(new File("./src/test/java/spoon/test/template/SuperTemplate.java")));
		Factory factory = new Factory(new DefaultCoreFactory(), new StandardEnvironment());
		comp.compileSrc(factory, files);
		comp.compileTemplate(factory, templates);

		CtClass<?> superc=factory.Class().get(SuperClass.class);
		Substitution.insertAll(superc, new SuperTemplate());
		
		CtMethod addedMethod = (CtMethod) superc.getElements(new NameFilter("toBeOverriden")).get(0);
		assertEquals("toBeOverriden", 
				addedMethod.getSimpleName()
				);
		
		CtClass<?> subc=factory.Class().get(SubClass.class);
		Substitution.insertAll(subc, new SubTemplate());
		CtMethod addedMethod2 = (CtMethod) subc.getElements(new NameFilter("toBeOverriden")).get(0);
		assertEquals("toBeOverriden", 
				addedMethod2.getSimpleName()
				);
		assertEquals("super.toBeOverriden()", 
				addedMethod2.getBody().getStatements().get(0).toString()
				);

		
	}
	

}
