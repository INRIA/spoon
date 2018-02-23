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
import spoon.SpoonException;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.reflect.visitor.processors.CheckScannerTestProcessor;
import spoon.test.metamodel.MMMethod;
import spoon.test.metamodel.MMMethodKind;
import spoon.test.metamodel.MMType;
import spoon.test.metamodel.MMTypeKind;
import spoon.test.metamodel.SpoonMetaModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CtScannerTest {
	@Test
	public void testScannerContract() throws Exception {
		// contract: CtScanner must call enter and exit methods in each visit methods.
		final Launcher launcher = new Launcher();
		launcher.setArgs(new String[] {"--output-type", "nooutput" });
		launcher.getEnvironment().setNoClasspath(true);
		// interfaces.
		launcher.addInputResource("./src/main/java/spoon/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/reflect/reference");
		// implementations.
		launcher.addInputResource("./src/main/java/spoon/support/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/reference");
		launcher.addInputResource("./src/main/java/spoon/reflect/visitor/CtScanner.java");
		launcher.buildModel();

		launcher.getModel().processWith(new CheckScannerTestProcessor());
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
		// contract: CtScanner must visit all metamodel properties and use correct CtRole!
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/main/java/spoon/reflect/");
		launcher.run();
		
		CtTypeReference<?> ctElementRef = launcher.getFactory().createCtTypeReference(CtElement.class);
		CtTypeReference<?> ctRefRef = launcher.getFactory().createCtTypeReference(CtReference.class);

		CtClass<?> scannerCtClass = (CtClass<?>)launcher.getFactory().Type().get(CtScanner.class);
		
		List<String> problems = new ArrayList<>();
		Set<String> ignoredInvocations = new HashSet(Arrays.asList("scan", "enter", "exit"));
		
		SpoonMetaModel metaModel = new SpoonMetaModel(new File("./src/main/java"));
		
		//collect all scanner visit methods, to check if all were checked
		Map<String, CtMethod<?>> scannerVisitMethodsByName = new HashMap<>();
		scannerCtClass.getAllMethods().forEach(m -> {
			if(m.getSimpleName().startsWith("visit")) {
				scannerVisitMethodsByName.put(m.getSimpleName(), m);
			}
		});

		class Counter  { int nbChecks = 0; }
		Counter c = new Counter();
		for (MMType leafMmType : metaModel.getMMTypes()) {

			// we only consider leaf, actual classes of the metamodel (eg CtInvocation) and not abstract ones (eg CtModifiable)
			if (leafMmType.getKind() != MMTypeKind.LEAF) {
				continue;
			}

			CtMethod<?> visitMethod = scannerVisitMethodsByName.remove("visit"+leafMmType.getName());
			assertNotNull("CtScanner#" + "visit"+leafMmType.getName() + "(...) not found", visitMethod);
			Set<String> calledMethods = new HashSet<>();
			Set<String> checkedMethods = new HashSet<>();

			// go over the roles and the corresponding fields of this type
			leafMmType.getRole2field().forEach((role, mmField) -> {

				if (mmField.isDerived()) {
					//ignore derived fields
					return; // return of the lambda
				}

				// ignore fields, which doesn't return CtElement
				if (mmField.getItemValueType().isSubtypeOf(ctElementRef) == false) {
					return; // return of the lambda
				}

				MMMethod getter = mmField.getMethod(MMMethodKind.GET);
				checkedMethods.add(getter.getSignature());
				//System.out.println("checking "+m.getSignature() +" in "+visitMethod.getSignature());

				// now, we collect at least one invocation to this getter in the visit method
				CtInvocation invocation = visitMethod.filterChildren(new TypeFilter<CtInvocation>(CtInvocation.class) {
					@Override
					public boolean matches(CtInvocation element) {
						if(ignoredInvocations.contains(element.getExecutable().getSimpleName())) {
							return false;
						}
						calledMethods.add(element.getExecutable().getSignature());
						return super.matches(element) && element.getExecutable().getSimpleName().equals(getter.getName());
					}
				}).first();

				if(getter.getName().equals("getComments") && leafMmType.getModelInterface().isSubtypeOf(ctRefRef)) {
					//one cannot set comments on references see the @UnsettableProperty of CtReference#setComments
					return;
				}

				// contract: there ia at least one invocation to all non-derived, role-based getters in the visit method of the Scanner
				if (invocation == null) {
					problems.add("no "+getter.getSignature() +" in "+visitMethod);
				} else {
					c.nbChecks++;
					//System.out.println(invocation.toString());

					// contract: the scan method is called with the same role as the one set on field / property
					CtRole expectedRole = metaModel.getRoleOfMethod((CtMethod<?>)invocation.getExecutable().getDeclaration());
					CtInvocation<?> scanInvocation = invocation.getParent(CtInvocation.class);
					String realRoleName = ((CtFieldRead<?>) scanInvocation.getArguments().get(0)).getVariable().getSimpleName();
					if(expectedRole.name().equals(realRoleName) == false) {
						problems.add("Wrong role " + realRoleName + " used in " + scanInvocation.getPosition());
					}
				}

			});
			calledMethods.removeAll(checkedMethods);

			// contract: CtScanner only calls methods that have a role and the associated getter
			if (calledMethods.size() > 0) {
				problems.add("CtScanner " + visitMethod.getPosition() + " calls unexpected methods: "+calledMethods);
			}
		}

		// contract: all visit* methods in CtScanner have been checked
		if(scannerVisitMethodsByName.isEmpty() == false) {
			problems.add("These CtScanner visit methods were not checked: " + scannerVisitMethodsByName.keySet());
		}
		if(problems.size()>0) {
			fail(String.join("\n", problems));
		}
		assertTrue("not enough checks", c.nbChecks >= 200);
	}

	@Test
	public void testScan() throws Exception {
		// contract: all AST nodes are visisted through method "scan"
		Launcher launcher;
		launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("src/test/resources/noclasspath/draw2d");
		launcher.buildModel();
		class Counter {
			int nEnter=0;
			int nExit=0;
			int nObject=0;
			int nElement=0;
		};
		Counter counter = new Counter();
		launcher.getModel().getRootPackage().accept(new CtScanner() {
			@Override
			public void scan(Object o) {
				counter.nObject++;
				super.scan(o);
			}
			@Override
			public void scan(CtElement o) {
				counter.nElement++;
				super.scan(o);
			}
			@Override
			public void enter(CtElement o) {
				counter.nEnter++;
				super.enter(o);
			}
			@Override
			public void exit(CtElement o) {
				counter.nExit++;
				super.exit(o);
			}
		});
		// interesting, this is never called because of covariance, only CtElement or Collection is called
		assertEquals(0, counter.nObject);
		// this is a coarse-grain check to see if the scanner changes
		// no more exec ref in paramref
		assertEquals(3616, counter.nElement);
		assertEquals(2396, counter.nEnter);
		assertEquals(2396, counter.nExit);

	}
}
