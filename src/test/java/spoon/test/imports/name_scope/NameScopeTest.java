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
package spoon.test.imports.name_scope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.NameScope;
import spoon.reflect.visitor.NameScopeScanner;
import spoon.reflect.visitor.chain.CtScannerListener;
import spoon.reflect.visitor.chain.ScanningMode;
import spoon.test.imports.name_scope.testclasses.Renata;
import spoon.testing.utils.ModelUtils;

public class NameScopeTest {

	@Test
	public void testNameScopeScanner() throws Exception {
		//contract: check that NameScope knows expected name.
		CtType<?> typeRenata = ModelUtils.buildClass(launcher -> {
			//needed to compute imports
			launcher.getEnvironment().setAutoImports(true);
		}, Renata.class);
		
		CtField<?> fieldMichal = typeRenata.getField("michal");
		CtType<?> typeTereza = typeRenata.getNestedType("Tereza");
		
		CtMethod<?> methodDraw = typeTereza.getMethodsByName("draw").get(0);
		
		CtType<?> typeFile = typeRenata.getFactory().Type().createReference(File.class).getTypeDeclaration();
		CtType<?> typeSystem = typeRenata.getFactory().Type().createReference(System.class).getTypeDeclaration();
		CtMethod<?> methodCurrentTimeMillis = typeSystem.getMethodsByName("currentTimeMillis").get(0);
		
		CtType<?> typeFiles = typeRenata.getFactory().Type().createReference(Files.class).getTypeDeclaration();
		CtMethod<?> methodsNewDirectoryStream = typeFiles.getMethodsByName("newDirectoryStream").get(0);
		
		NameScopeScanner<Void> scanner = new NameScopeScanner<>();
		scanner.setVisitCompilationUnitContent(true);
		scanner.setListener(new CtScannerListener() {
			@Override
			public ScanningMode enter(CtRole role, CtElement element) {
				if (element instanceof CtLiteral) {
					CtLiteral<String> literal = (CtLiteral<String>) element;
					//check that NameScope is aware of all names, which are visible at position of the literals
					if ("1".equals(literal.getValue())) {
						//contract: the local variables are visible after they are declared
						assertNameScope(Arrays.asList(), scanner.getNameScope(), "count");
						assertNameScope(Arrays.asList("String theme"), scanner.getNameScope(), "theme");
						assertNameScope(Arrays.asList(methodDraw), scanner.getNameScope(), "draw");
						assertNameScope(Arrays.asList(typeTereza), scanner.getNameScope(), "Tereza");
						assertNameScope(Arrays.asList(fieldMichal), scanner.getNameScope(), "michal");
						assertNameScope(Arrays.asList(typeRenata), scanner.getNameScope(), "Renata");
						//contract: imported types are visible too
						assertNameScope(Arrays.asList(typeFile), scanner.getNameScope(), "File");
						//contract: imported static methods are visible too
						assertNameScope(Arrays.asList(methodCurrentTimeMillis), scanner.getNameScope(), "currentTimeMillis");
						//contract: type members imported by wildcard are visible too
						assertNameScope(Arrays.asList(methodsNewDirectoryStream), scanner.getNameScope(), "newDirectoryStream");
						//contract: The names are case sensitive
						assertNameScope(Arrays.asList(), scanner.getNameScope(), "Michal");
						//the names which are not visible, must not be returned
						assertNameScope(Arrays.asList(), scanner.getNameScope(), "void");
						assertNameScope(Arrays.asList(), scanner.getNameScope(), "String");
						//type members of System are not visible
						assertNameScope(Arrays.asList(), scanner.getNameScope(), "setIn");
						//type member itself whose field is imported is not visible
						assertNameScope(Arrays.asList(), scanner.getNameScope(), "System");
						//type member itself whose type members are imported by wildcard are not visible
						assertNameScope(Arrays.asList(), scanner.getNameScope(), "Fields");
					} else if ("2".equals(literal.getValue())) {
						//contract: the local variables are visible after they are declared
						assertNameScope(Arrays.asList("int count"), scanner.getNameScope(), "count");
					}
				}
				return ScanningMode.NORMAL;
			}
		});
		scanner.scan(typeRenata.getPosition().getCompilationUnit());
	}
	
	private void assertNameScope(List<?> expectedElements, NameScope nameScope, String name) {
		List<CtElement> realElements = new ArrayList<>();
		nameScope.forEachElementByName(name, e -> realElements.add(e));
		assertEquals(expectedElements.size(), realElements.size());
		for (int i = 0; i < expectedElements.size(); i++) {
			Object expected = expectedElements.get(i);
			assertNotNull(expected);
			if (expected instanceof String) {
				String expectedString = (String) expected;
				assertEquals(expectedString, realElements.get(i).toString());
			} else {
				assertSame(expected, realElements.get(i));
			}
		}
	}
}
