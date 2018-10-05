/**
 * Copyright (C) 2006-2018 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
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
