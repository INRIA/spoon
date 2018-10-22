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
package spoon.test.interfaces;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.support.reflect.CtExtendedModifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestInterfaceWithoutSetup {

	@Test
	public void testModifierFromInterfaceFieldAndMethod() {
		// contract: methods defined in interface are all public and fields are all public and static
		Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/resources/spoon/test/itf/DumbItf.java");
		spoon.getEnvironment().setNoClasspath(false);
		spoon.buildModel();

		CtType dumbType = spoon.getFactory().Type().get("toto.DumbItf");

		assertEquals(2, dumbType.getFields().size());

		CtField fieldImplicit = dumbType.getField("CONSTANT_INT");

		Set<CtExtendedModifier> extendedModifierSet = fieldImplicit.getExtendedModifiers();
		assertEquals(3, extendedModifierSet.size());
		assertTrue(extendedModifierSet.contains(new CtExtendedModifier(ModifierKind.FINAL, true)));
		assertTrue(extendedModifierSet.contains(new CtExtendedModifier(ModifierKind.PUBLIC, true)));
		assertTrue(extendedModifierSet.contains(new CtExtendedModifier(ModifierKind.STATIC, true)));

		for (CtExtendedModifier extendedModifier : extendedModifierSet) {
			assertTrue(extendedModifier.isImplicit());
		}

		assertEquals(ModifierKind.PUBLIC, fieldImplicit.getVisibility());
		assertTrue(fieldImplicit.hasModifier(ModifierKind.STATIC));
		assertTrue(fieldImplicit.hasModifier(ModifierKind.PUBLIC));
		assertTrue(fieldImplicit.hasModifier(ModifierKind.FINAL));

		CtField fieldExplicit = dumbType.getField("ANOTHER_INT");

		extendedModifierSet = fieldExplicit.getExtendedModifiers();
		assertEquals(3, extendedModifierSet.size());
		assertTrue(extendedModifierSet.contains(new CtExtendedModifier(ModifierKind.FINAL, true)));
		assertTrue(extendedModifierSet.contains(new CtExtendedModifier(ModifierKind.PUBLIC, false)));
		assertTrue(extendedModifierSet.contains(new CtExtendedModifier(ModifierKind.STATIC, false)));

		int counter = 0;
		for (CtExtendedModifier extendedModifier : extendedModifierSet) {
			if (extendedModifier.getKind() == ModifierKind.FINAL) {
				assertTrue(extendedModifier.isImplicit());
				counter++;
			} else {
				assertFalse(extendedModifier.isImplicit());
				assertTrue(extendedModifier.getPosition().isValidPosition());
				assertEquals(extendedModifier.getKind().toString(), extendedModifier.getPosition().getCompilationUnit().getOriginalSourceCode().substring(extendedModifier.getPosition().getSourceStart(), extendedModifier.getPosition().getSourceEnd() + 1));
				counter++;
			}
		}

		assertEquals(3, counter);

		assertEquals(ModifierKind.PUBLIC, fieldExplicit.getVisibility());
		assertTrue(fieldExplicit.hasModifier(ModifierKind.STATIC));
		assertTrue(fieldExplicit.hasModifier(ModifierKind.PUBLIC));
		assertTrue(fieldExplicit.hasModifier(ModifierKind.FINAL));

		assertEquals(4, dumbType.getMethods().size());

		CtMethod staticMethod = (CtMethod) dumbType.getMethodsByName("foo").get(0);
		assertTrue(staticMethod.hasModifier(ModifierKind.PUBLIC));
		assertTrue(staticMethod.hasModifier(ModifierKind.STATIC));

		extendedModifierSet = staticMethod.getExtendedModifiers();
		assertEquals(2, extendedModifierSet.size());
		assertTrue(extendedModifierSet.contains(new CtExtendedModifier(ModifierKind.PUBLIC, true)));
		assertTrue(extendedModifierSet.contains(new CtExtendedModifier(ModifierKind.STATIC, false)));

		CtMethod publicMethod = (CtMethod) dumbType.getMethodsByName("machin").get(0);
		assertTrue(publicMethod.hasModifier(ModifierKind.PUBLIC));
		assertFalse(publicMethod.hasModifier(ModifierKind.STATIC));

		extendedModifierSet = publicMethod.getExtendedModifiers();
		assertEquals(2, extendedModifierSet.size());
		assertTrue(extendedModifierSet.contains(new CtExtendedModifier(ModifierKind.PUBLIC, true)));
		assertTrue(extendedModifierSet.contains(new CtExtendedModifier(ModifierKind.ABSTRACT, true)));

		CtMethod defaultMethod = (CtMethod) dumbType.getMethodsByName("bla").get(0);
		assertTrue(defaultMethod.hasModifier(ModifierKind.PUBLIC));
		assertTrue(defaultMethod.isDefaultMethod());
		assertFalse(defaultMethod.hasModifier(ModifierKind.STATIC));

		extendedModifierSet = defaultMethod.getExtendedModifiers();
		assertEquals(1, extendedModifierSet.size());
		assertTrue(extendedModifierSet.contains(new CtExtendedModifier(ModifierKind.PUBLIC, true)));

		CtMethod explicitDefaultMethod = (CtMethod) dumbType.getMethodsByName("anotherOne").get(0);
		assertTrue(explicitDefaultMethod.hasModifier(ModifierKind.PUBLIC));

		extendedModifierSet = explicitDefaultMethod.getExtendedModifiers();
		assertEquals(2, extendedModifierSet.size());
		assertTrue(extendedModifierSet.contains(new CtExtendedModifier(ModifierKind.PUBLIC, false)));
		assertTrue(extendedModifierSet.contains(new CtExtendedModifier(ModifierKind.ABSTRACT, true)));
	}

	@Test
	public void testInterfacePrettyPrinting() throws IOException {
		// contract: only explicit modifiers are pretty printed
		String originalFilePath = "./src/test/resources/spoon/test/itf/DumbItf.java";
		String targetDir = "./target/spoon-dumbitf";

		Launcher spoon = new Launcher();
		spoon.addInputResource(originalFilePath);
		spoon.getEnvironment().setCommentEnabled(true);
		spoon.getEnvironment().setShouldCompile(true);
		spoon.getEnvironment().setAutoImports(true);
		spoon.setSourceOutputDirectory(targetDir);
		spoon.run();

		String originalFile = StringUtils.join(Files.readAllLines(new File(originalFilePath).toPath()), "\n").replaceAll("\\s", "");
		String prettyPrintedFile = StringUtils.join(Files.readAllLines(new File(targetDir + "/toto/DumbItf.java").toPath()), "\n").replaceAll("\\s", "");

		assertEquals(originalFile, prettyPrintedFile);
	}
}
