/*
 * Copyright (C) 2006-2015 INRIA and contributors
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

package spoon.reflect.visitor;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.reflect.visitor.processors.CheckScannerProcessor;
import spoon.test.SpoonTestHelpers;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static spoon.test.SpoonTestHelpers.isMetamodelProperty;

public class CtScannerTest {
	@Test
	public void testScannerContract() throws Exception {
		// contract: CtScanner must call enter and exit methods in each visit methods.
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addProcessor(new CheckScannerProcessor());
		// interfaces.
		launcher.addInputResource("./src/main/java/spoon/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/reflect/reference");
		// implementations.
		launcher.addInputResource("./src/main/java/spoon/support/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/reference");
		launcher.addInputResource("./src/main/java/spoon/reflect/visitor/CtScanner.java");
		launcher.run();

		// All assertions are in the processor.
	}

	class SimpleSignature extends CtScanner {
		String signature = "";
		@Override
		public <T> void visitCtParameter(CtParameter<T> parameter) {
			signature += parameter.getType().getQualifiedName()+", ";
			super.visitCtParameter(parameter);
		}

		@Override
		public <T> void visitCtMethod(CtMethod<T> m) {
			signature += m.getSimpleName()+"(";
			super.visitCtMethod(m);
			signature += ")";
		}
	}


	class SimpleSignatureComparator implements Comparator<CtMethod<?>> {
		@Override
		public int compare(CtMethod<?> o1, CtMethod<?> o2) {
			return computeSimpleSignature(o1).compareTo(computeSimpleSignature(o2));
		}
	}

	private String computeSimpleSignature(CtMethod<?> m) {
		SimpleSignature sc1 = new SimpleSignature();
		sc1.visitCtMethod(m);
		return sc1.signature;
	}

	@Test
	public void testScannerCallsAllProperties() throws Exception {
		// contract: CtScanner must visit all metamodel properties
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/main/java/spoon/reflect/");
		launcher.run();
		CtClass<?> scanner = (CtClass<?>)launcher.getFactory().Type().get(CtScanner.class);

		for (CtType<?> t : SpoonTestHelpers.getAllInstantiableMetamodelInterfaces()) {
			Set<String> t1 = new TreeSet<>();
			for (CtMethod<?> m : t.getAllMethods()) {
				if (isMetamodelProperty(t, m)) {
					t1.add(computeSimpleSignature(m));
				}
			}

			Set<String> t2 = new TreeSet<>();
			CtMethod<?> visitMethod = scanner.getMethodsByName("visit"+t.getSimpleName()).get(0);
			for (CtInvocation<?> invoc : visitMethod.getElements(new TypeFilter<CtInvocation>(CtInvocation.class) {
					@Override
					public boolean matches(CtInvocation element) {
						CtMethod<?> method = (CtMethod<?>) element.getExecutable().getExecutableDeclaration();
						assertFalse(method.isShadow());
						return super.matches(element) && isMetamodelProperty(t, method);
					}
				})) {
				t2.add(computeSimpleSignature((CtMethod<?>) invoc.getExecutable().getExecutableDeclaration()));
			}
		assertEquals("CtScanner contract violated for "+t.getSimpleName(), t1, t2);
		}
	}


}
