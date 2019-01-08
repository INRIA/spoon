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

import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.LexicalScope;
import spoon.reflect.visitor.LexicalScopeBuilder;
import spoon.test.imports.name_scope.testclasses.Renata;
import spoon.testing.utils.ModelUtils;

public class NameScopeTest {

	@Test
	public void testNameScopeScanner() throws Exception {
		//contract: check that LexicalScope knows expected name.
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
		
		LexicalScopeBuilder scanner = new LexicalScopeBuilder();
		scanner.setVisitCompilationUnitContent(true);
		// we collect all scopes
		scanner.scan(typeRenata.getPosition().getCompilationUnit());

		// we have 8 scopes in Renata
		List<LexicalScope> lexicalScopes = scanner.getNameScopes();
		assertEquals(8, lexicalScopes.size());

		LexicalScope n1 = getNameScope(scanner, "draw");

		//contract: the local variables are visible after they are declared
		checkThatScopeContains(n1, Arrays.asList(), "count");
		checkThatScopeContains(n1, Arrays.asList("String theme"), "theme");
		checkThatScopeContains(n1, Arrays.asList(methodDraw), "draw");
		checkThatScopeContains(n1, Arrays.asList(typeTereza), "Tereza");
		checkThatScopeContains(n1, Arrays.asList(fieldMichal), "michal");
		checkThatScopeContains(n1, Arrays.asList(typeRenata), "Renata");
		//contract: imported types are visible too
		checkThatScopeContains(n1, Arrays.asList(typeFile), "File");
		//contract: imported static methods are visible too
		checkThatScopeContains(n1, Arrays.asList(methodCurrentTimeMillis), "currentTimeMillis");
		//contract: type members imported by wildcard are visible too
		checkThatScopeContains(n1, Arrays.asList(methodsNewDirectoryStream), "newDirectoryStream");
		//contract: The names are case sensitive
		checkThatScopeContains(n1, Arrays.asList(), "Michal");
		//the names which are not visible, must not be returned
		checkThatScopeContains(n1, Arrays.asList(), "void");
		checkThatScopeContains(n1, Arrays.asList(), "String");
		//type members of System are not visible
		checkThatScopeContains(n1, Arrays.asList(), "setIn");
		//type member itself whose field is imported is not visible
		checkThatScopeContains(n1, Arrays.asList(), "System");
		//type member itself whose type members are imported by wildcard are not visible
		checkThatScopeContains(n1, Arrays.asList(), "Fields");

		//contract: the local variables is only visible in the block scope (not the method one)
		checkThatScopeContains(n1, Arrays.asList(), "count");
		checkThatScopeContains(lexicalScopes.get(7), Arrays.asList("int count"), "count");
	}


	private LexicalScope getNameScope(LexicalScopeBuilder builder, String name) {
		for (LexicalScope n: builder.getNameScopes()) {
			System.out.println(n.getScopeElement().toString());
			if (n.getScopeElement() instanceof CtNamedElement && ((CtNamedElement)n.getScopeElement()).getSimpleName().equals(name)) {
				return n;
			}
		}
		throw  new IllegalStateException();
	}


	private void checkThatScopeContains(LexicalScope lexicalScope, List<?> expectedElements, String name) {
		List<CtElement> realElements = new ArrayList<>();
		lexicalScope.forEachElementByName(name, e -> realElements.add(e));
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
