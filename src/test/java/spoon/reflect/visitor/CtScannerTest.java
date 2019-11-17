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
package spoon.reflect.visitor;

import org.junit.Test;
import spoon.Launcher;
import spoon.metamodel.MMMethod;
import spoon.metamodel.MMMethodKind;
import spoon.metamodel.ConceptKind;
import spoon.metamodel.MetamodelConcept;
import spoon.metamodel.Metamodel;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.meta.ContainerKind;
import spoon.reflect.meta.RoleHandler;
import spoon.reflect.meta.impl.RoleHandlerHelper;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.reflect.visitor.processors.CheckScannerTestProcessor;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CtScannerTest {

	@Test
	public void testScannerContract() {
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
			signature += parameter.getType().getQualifiedName() + ", ";
			super.visitCtParameter(parameter);
		}

		@Override
		public <T> void visitCtMethod(CtMethod<T> m) {
			signature += m.getSimpleName() + "(";
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
	public void testScannerCallsAllProperties() {
		// contract: CtScanner must visit all metamodel properties and use correct CtRole!
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/main/java/spoon/reflect/");
		launcher.run();

		CtTypeReference<?> ctElementRef = launcher.getFactory().createCtTypeReference(CtElement.class);
		CtTypeReference<?> ctRefRef = launcher.getFactory().createCtTypeReference(CtReference.class);

		CtClass<?> scannerCtClass = (CtClass<?>) launcher.getFactory().Type().get(CtScanner.class);

		List<String> problems = new ArrayList<>();
		Set<String> ignoredInvocations = new HashSet(Arrays.asList("scan", "enter", "exit"));

		Metamodel metaModel = Metamodel.getInstance();

		//collect all scanner visit methods, to check if all were checked
		Map<String, CtMethod<?>> scannerVisitMethodsByName = new HashMap<>();
		scannerCtClass.getAllMethods().forEach(m -> {
			if (m.getSimpleName().startsWith("visit")) {
				scannerVisitMethodsByName.put(m.getSimpleName(), m);
			}
		});

		class Counter  { int nbChecks = 0; }
		Counter c = new Counter();
		for (MetamodelConcept leafConcept : metaModel.getConcepts()) {

			// we only consider leaf, actual classes of the metamodel (eg CtInvocation) and not abstract ones (eg CtModifiable)
			if (leafConcept.getKind() != ConceptKind.LEAF) {
				continue;
			}

			CtMethod<?> visitMethod = scannerVisitMethodsByName.remove("visit" + leafConcept.getName());
			assertNotNull("CtScanner#" + "visit" + leafConcept.getName() + "(...) not found", visitMethod);
			Set<String> calledMethods = new HashSet<>();
			Set<String> checkedMethods = new HashSet<>();

			// go over the roles and the corresponding fields of this type
			leafConcept.getRoleToProperty().forEach((role, mmField) -> {

				if (mmField.isDerived() || mmField.isUnsettable()) {
					//ignore derived or unsettable fields
					return; // return of the lambda
				}

				// ignore fields, which doesn't return CtElement
				if (mmField.getTypeofItems().isSubtypeOf(ctElementRef) == false) {
					return; // return of the lambda
				}

				MMMethod getter = mmField.getMethod(MMMethodKind.GET);
				checkedMethods.add(getter.getSignature());
				//System.out.println("checking "+m.getSignature() +" in "+visitMethod.getSignature());

				// now, we collect at least one invocation to this getter in the visit method
				CtInvocation invocation = visitMethod.filterChildren(new TypeFilter<CtInvocation>(CtInvocation.class) {
					@Override
					public boolean matches(CtInvocation element) {
						if (ignoredInvocations.contains(element.getExecutable().getSimpleName())) {
							return false;
						}
						calledMethods.add(element.getExecutable().getSignature());
						return super.matches(element) && element.getExecutable().getSimpleName().equals(getter.getName());
					}
				}).first();

				if ("getComments".equals(getter.getName()) && leafConcept.getMetamodelInterface().isSubtypeOf(ctRefRef)) {
					//one cannot set comments on references see the @UnsettableProperty of CtReference#setComments
					return;
				}

				// contract: there ia at least one invocation to all non-derived, role-based getters in the visit method of the Scanner
				if (invocation == null) {
					problems.add("no " + getter.getSignature() + " in " + visitMethod);
				} else {
					c.nbChecks++;
					//System.out.println(invocation.toString());

					// contract: the scan method is called with the same role as the one set on field / property
					CtRole expectedRole = metaModel.getRoleOfMethod((CtMethod<?>) invocation.getExecutable().getDeclaration());
					CtInvocation<?> scanInvocation = invocation.getParent(CtInvocation.class);
					String realRoleName = ((CtFieldRead<?>) scanInvocation.getArguments().get(0)).getVariable().getSimpleName();
					if (expectedRole.name().equals(realRoleName) == false) {
						problems.add("Wrong role " + realRoleName + " used in " + scanInvocation.getPosition());
					}
				}

			});
			calledMethods.removeAll(checkedMethods);

			// contract: CtScanner only calls methods that have a role and the associated getter
			if (!calledMethods.isEmpty()) {
				problems.add("CtScanner " + visitMethod.getPosition() + " calls unexpected methods: " + calledMethods);
			}
		}

		// contract: all visit* methods in CtScanner have been checked
		if (scannerVisitMethodsByName.isEmpty() == false) {
			problems.add("These CtScanner visit methods were not checked: " + scannerVisitMethodsByName.keySet());
		}
		if (!problems.isEmpty()) {
			fail(String.join("\n", problems));
		}
		assertTrue("not enough checks " + c.nbChecks, c.nbChecks >= 200);
	}

	@Test
	public void testScan() {
		// contract: all AST nodes are visited through method "scan"
		Launcher launcher;
		launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("src/test/resources/noclasspath/draw2d");
		launcher.buildModel();
		class Counter {
			int nEnter = 0;
			int nExit = 0;
			int nObject = 0;
			int nElement = 0;
			Deque<CollectionContext> contexts = new ArrayDeque<>();
		}
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
		
		//top comment of file belongs to compilation unit which is not visited by standard scanning
		//so count comments of these compilation units
		int countOfCommentsInCompilationUnits = 0;
		for (CompilationUnit cu : launcher.getFactory().CompilationUnit().getMap().values()) {
			countOfCommentsInCompilationUnits += cu.getComments().size();
		}

		// interesting, this is never called because of covariance, only CtElement or Collection is called
		assertEquals(0, counter.nObject);
		// this is a coarse-grain check to see if the scanner changes
		// no more exec ref in paramref
		// also takes into account the comments
		assertEquals(3655, counter.nElement + countOfCommentsInCompilationUnits);
		assertEquals(2435, counter.nEnter + countOfCommentsInCompilationUnits);
		assertEquals(2435, counter.nExit + countOfCommentsInCompilationUnits);

		// contract: all AST nodes which are part of Collection or Map are visited first by method "scan(Collection|Map)" and then by method "scan(CtElement)"
		Counter counter2 = new Counter();
		launcher.getModel().getRootPackage().accept(new CtScanner() {
			@Override
			public void scan(Object o) {
				counter2.nObject++;
				super.scan(o);
			}
			@Override
			public void scan(CtRole role, CtElement o) {
				if (o == null) {
					//there is no collection involved in scanning of this single value NULL attribute
					assertNull(counter2.contexts.peek().col);

				} else {
					RoleHandler rh = RoleHandlerHelper.getRoleHandler(o.getParent().getClass(), role);
					if (rh.getContainerKind() == ContainerKind.SINGLE) {
						//there is no collection involved in scanning of this single value attribute
						assertNull(counter2.contexts.peek().col);
					} else {
						counter2.contexts.peek().assertRemoveSame(o);
					}
				}
				counter2.nElement++;
				super.scan(o);
			}
			@Override
			public void scan(CtRole role, Collection<? extends CtElement> elements) {
				//contract: before processed collection is finished before it starts with next collection
				counter2.contexts.peek().initCollection(elements);
				super.scan(role, elements);
				//contract: all elements of collection are processed in previous super.scan call
				counter2.contexts.peek().assertCollectionIsEmpty();
			}
			@Override
			public void scan(CtRole role, Map<String, ? extends CtElement> elements) {
				//contract: before processed collection is finished before it starts with next collection
				counter2.contexts.peek().initCollection(elements.values());
				super.scan(role, elements);
				//contract: all elements of collection are processed in previous super.scan call
				counter2.contexts.peek().assertCollectionIsEmpty();
			}
			@Override
			public void enter(CtElement o) {
				counter2.nEnter++;
				counter2.contexts.push(new CollectionContext());
			}
			@Override
			public void exit(CtElement o) {
				counter2.nExit++;
				counter2.contexts.peek().assertCollectionIsEmpty();
				counter2.contexts.pop();
			}
		});
		assertEquals(counter.nObject, counter2.nObject);
		assertEquals(counter.nElement, counter2.nElement);
		assertEquals(counter.nEnter, counter2.nEnter);
		assertEquals(counter.nExit, counter2.nExit);
	}
	private static class CollectionContext {
		Collection<CtElement> col;
		void assertCollectionIsEmpty() {
			assertTrue(col == null || col.isEmpty());
			col = null;
		}
		public void initCollection(Collection<? extends CtElement> elements) {
			assertCollectionIsEmpty();
			col = new ArrayList<>(elements);
			assertFalse(col.contains(null));
		}
		public void assertRemoveSame(CtElement o) {
			assertNotNull(col);
			for (Iterator iter = col.iterator(); iter.hasNext();) {
				CtElement ctElement = (CtElement) iter.next();
				if (o == ctElement) {
					iter.remove();
					return;
				}
			}
			fail();
		}
	}
}
