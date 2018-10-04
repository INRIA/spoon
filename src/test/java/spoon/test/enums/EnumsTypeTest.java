package spoon.test.enums;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import spoon.Launcher;
import spoon.compiler.SpoonResource;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;

public class EnumsTypeTest {

	@Test
	public void testEnumsType() throws Exception {
		// contract: shadow enum should still be considered as an enum
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/reference-test/EnumsRef.java");

		Factory factory = launcher.getFactory();
		List<SpoonResource> classpath = SpoonResourceHelper.resources("./src/test/resources/reference-test/EnumJar.jar");
		String[] dependencyClasspath = { classpath.get(0).getPath() };
		factory.getEnvironment().setSourceClasspath(dependencyClasspath);
		assertEquals(1, classpath.size());

		launcher.buildModel();

		List<CtAssignment> assignments = Query.getElements(factory, new TypeFilter<>(CtAssignment.class));

		CtTypeReference typeRefFromSource = assignments.get(0).getType();
		CtType typeFromSource = typeRefFromSource.getTypeDeclaration();
		assertTrue(typeRefFromSource.isEnum());
		assertTrue(typeFromSource.isEnum());
		assertTrue(typeFromSource instanceof CtEnum);

		CtTypeReference typeRefFromJar = assignments.get(1).getType();
		CtType typeFromJar = typeRefFromJar.getTypeDeclaration();
		assertTrue(typeRefFromJar.isEnum());
		assertTrue(typeFromJar.isEnum());
		assertTrue(typeFromJar instanceof CtEnum);
	}

	@Test
	public void testEnumsFromInterface() throws Exception {
		// contract: shadow enum from an interface should still be considered as an enum
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/reference-test/InterfaceWithEnum.java");
		launcher.addInputResource("./src/test/resources/reference-test/InterfaceEnumRef.java");

		Factory factory = launcher.getFactory();
		List<SpoonResource> classpath = SpoonResourceHelper.resources("./src/test/resources/reference-test/InterfaceWithEnumJar.jar");
		String[] dependencyClasspath = { classpath.get(0).getPath() };
		factory.getEnvironment().setSourceClasspath(dependencyClasspath);
		assertEquals(1, classpath.size());

		launcher.buildModel();

		List<CtAssignment> assignments = Query.getElements(factory, new TypeFilter<>(CtAssignment.class));

		CtTypeReference typeRefFromSource = assignments.get(0).getType();
		CtType typeFromSource = typeRefFromSource.getTypeDeclaration();
		assertTrue(typeRefFromSource.isEnum());
		assertTrue(typeFromSource.isEnum());
		assertTrue(typeFromSource instanceof CtEnum);

		CtTypeReference typeRefFromJar = assignments.get(1).getType();
		CtType typeFromJar = typeRefFromJar.getTypeDeclaration();
		assertTrue(typeRefFromJar.isEnum()); // fail
		assertTrue(typeFromJar.isEnum()); // fail
		assertTrue(typeFromJar instanceof CtEnum); // fail
	}
}
