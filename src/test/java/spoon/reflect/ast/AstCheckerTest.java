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
package spoon.reflect.ast;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.reference.CtExecutableReference;
import spoon.support.modelobs.FineModelChangeListener;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtThrow;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;
import spoon.support.comparator.CtLineElementComparator;
import spoon.support.util.ModelList;
import spoon.support.util.ModelSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class AstCheckerTest {

	@Test
	public void testExecutableReference() {

		Launcher l = new Launcher();
		l.getEnvironment().setNoClasspath(true);
		l.addInputResource("./src/test/resources/noclasspath/A4.java");
		l.buildModel();

		CtClass<Object> klass = l.getFactory().Class().get("A4");
		CtMethod<?> bMethod = klass.getMethodsByName("b").get(0);
		CtMethod<?> cMethod = klass.getMethodsByName("c").get(0);
		List<CtExecutableReference> elements = cMethod.getElements(new TypeFilter<>(CtExecutableReference.class));
		CtExecutableReference methodRef = elements.stream().filter(e -> e.getSimpleName().equals("b")).findFirst().get();

		//contract: Executable reference declaring type and return type match those of the actual executable
		assertEquals(bMethod.getType().getTypeDeclaration(),methodRef.getType().getTypeDeclaration());
		assertEquals(bMethod.getDeclaringType(),methodRef.getDeclaringType().getTypeDeclaration());
	}

	@Test
	public void testAvoidSetCollectionSavedOnAST() {
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		launcher.addInputResource("src/main/java");
		launcher.buildModel();

		final Factory factory = launcher.getFactory();
		final List<CtTypeReference<?>> collectionsRef = Arrays.asList(
				factory.Type().createReference(Collection.class),
				factory.Type().createReference(List.class),
				factory.Type().createReference(Set.class),
				factory.Type().createReference(Map.class));

		final List<CtInvocation<?>> invocations = Query.getElements(factory, new TypeFilter<CtInvocation<?>>(CtInvocation.class) {
			@Override
			public boolean matches(CtInvocation<?> element) {
				if (!(element.getParent() instanceof CtInvocation)) {
					return false;
				}
				final CtInvocation<?> parent = (CtInvocation<?>) element.getParent();
				if (parent.getTarget() == null || !parent.getTarget().equals(element)) {
					return false;
				}
				if (!element.getExecutable().getDeclaringType().getSimpleName().startsWith("Ct")) {
					return false;
				}
				boolean isDataStructure = false;
				for (CtTypeReference<?> ctTypeReference : collectionsRef) {
					if (element.getType().isSubtypeOf(ctTypeReference)) {
						isDataStructure = true;
						break;
					}
				}
				if (!isDataStructure) {
					return false;
				}

				final String simpleName = parent.getExecutable().getSimpleName();
				return simpleName.startsWith("add") || simpleName.startsWith("remove") || simpleName.startsWith("put");
			}
		});
		if (!invocations.isEmpty()) {
			final String error = invocations.stream() //
					.sorted(new CtLineElementComparator()) //
					.map(i -> "see " + i.getPosition().getFile().getAbsoluteFile() + " at " + i.getPosition().getLine()) //
					.collect(Collectors.joining(",\n"));
			throw new AssertionError(error);
		}
	}

	@Test
	public void testPushToStackChanges() {
		// contract: setters should check the given parameters against NPE and the ModelChangeListener must be called!
		final Launcher launcher = new Launcher();
		launcher.getEnvironment().setNoClasspath(true);
		// Implementations.
		launcher.addInputResource("./src/main/java/spoon/support/reflect/CtModifierHandler.java");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/code");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/declaration");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/reference");
		launcher.addInputResource("./src/main/java/spoon/support/reflect/internal");
		// Utils.
		launcher.addInputResource("./src/test/java/spoon/reflect/ast/AstCheckerTest.java");
		launcher.buildModel();

		final PushStackInIntercessionChecker checker = new PushStackInIntercessionChecker();
		checker.scan(launcher.getModel().getRootPackage());
		if (!checker.result.isEmpty()) {
			System.err.println(checker.count);
			throw new AssertionError(checker.result);
		}
	}

	private class PushStackInIntercessionChecker extends CtScanner {
		private final List<String> notCandidates;
		private String result = "";
		private int count;

		PushStackInIntercessionChecker() {
			notCandidates = Arrays.asList(
					"CtTypeImpl#setTypeMembers",
					"CtStatementListImpl#setPosition",
					"CtElementImpl#setFactory",
					"CtElementImpl#setPositions",
					"CtElementImpl#setDocComment",
					"CtElementImpl#setParent",
					"CtElementImpl#setAllMetadata",
					"CtElementImpl#setValueByRole",
					"CtTypeParameterReferenceImpl#addBound",
					"CtTypeParameterReferenceImpl#removeBound",
					"CtWildcardReferenceImpl#setBounds",
					"CtModuleImpl#addUsedService",
					"CtModuleImpl#addExportedPackage",
					"CtModuleImpl#addOpenedPackage",
					"CtModuleImpl#addRequiredModule",
					"CtModuleImpl#addProvidedService",
					"CtArrayTypeReferenceImpl#setSimpleName",
					"CtCompilationUnitImpl#addDeclaredType",
					"CtCompilationUnitImpl#setFile",
					"CtCompilationUnitImpl#setLineSeparatorPositions"
			);
		}

		private boolean isToBeProcessed(CtMethod<?> candidate) {
			if (candidate.getAnnotation(UnsettableProperty.class) != null) {
				return false;
			}
			if (candidate.getAnnotation(DerivedProperty.class) != null) {
				return false;
			}
			return candidate.getBody() != null //
					&& !candidate.getParameters().isEmpty() //
					&& candidate.hasModifier(ModifierKind.PUBLIC) //
					&& (candidate.getSimpleName().startsWith("add")
						|| candidate.getSimpleName().startsWith("set")
						|| candidate.getSimpleName().startsWith("remove")) //
					&& candidate.getDeclaringType().getSimpleName().startsWith("Ct") //
					&& !isNotCandidate(candidate) //
					&& !isSurcharged(candidate) //
					&& !isDelegateMethod(candidate) //
					&& !isUnsupported(candidate.getBody()) //
					&& !isCallModelCollection(candidate.getBody()) //
					&& !hasPushToStackInvocation(candidate.getBody());
		}

		private boolean isNotCandidate(CtMethod<?> candidate) {
			return "setVisibility".equals(candidate.getSimpleName())
					|| notCandidates.contains(candidate.getDeclaringType().getSimpleName() + "#" + candidate.getSimpleName());
		}

		private boolean isSurcharged(CtMethod<?> candidate) {
			CtBlock<?> block = candidate.getBody();
			if (block.getStatements().isEmpty()) {
				return false;
			}
			CtInvocation potentialDelegate;
			if (block.getLastStatement() instanceof CtReturn) {
				if (!(((CtReturn) block.getLastStatement()).getReturnedExpression() instanceof CtInvocation)) {
					if (block.getStatement(0) instanceof CtInvocation) {
						potentialDelegate = block.getStatement(0);
					} else {
						return false;
					}
				} else {
					potentialDelegate = (CtInvocation) ((CtReturn) block.getLastStatement()).getReturnedExpression();
				}
			} else if (block.getStatement(0) instanceof CtInvocation && block.getStatements().size() == 1) {
				potentialDelegate = block.getStatement(0);
			} else {
				return false;
			}
			CtExecutable declaration = potentialDelegate.getExecutable().getDeclaration();
			if (!(declaration instanceof CtMethod)) {
				return false;
			}
			// check if the invocation has a model change listener
			return !isToBeProcessed((CtMethod<?>) declaration);
		}

		private boolean isDelegateMethod(CtMethod<?> candidate) {
			if (candidate.getBody().getStatements().isEmpty()) {
				return false;
			}
			if (!(candidate.getBody().getStatement(0) instanceof CtIf)) {
				return false;
			}
			if (!(((CtIf) candidate.getBody().getStatement(0)).getThenStatement() instanceof CtBlock)) {
				return false;
			}
			final CtBlock block = ((CtIf) candidate.getBody().getStatement(0)).getThenStatement();
			if (!(block.getStatement(0) instanceof CtInvocation || block.getStatement(0) instanceof CtReturn)) {
				return false;
			}
			CtInvocation potentialDelegate;
			if (block.getStatement(0) instanceof CtReturn) {
				if (!(((CtReturn) block.getStatement(0)).getReturnedExpression() instanceof CtInvocation)) {
					return false;
				}
				potentialDelegate = (CtInvocation) ((CtReturn) block.getStatement(0)).getReturnedExpression();
			} else {
				potentialDelegate = (CtInvocation) block.getStatement(0);
			}
			return potentialDelegate.getExecutable().getSimpleName().equals(candidate.getSimpleName());
		}

		private boolean isUnsupported(CtBlock<?> body) {
			return !body.getStatements().isEmpty() //
					&& body.getStatements().get(0) instanceof CtThrow //
					&& "UnsupportedOperationException".equals(((CtThrow) body.getStatements().get(0)).getThrownExpression().getType().getSimpleName());
		}

		private boolean isCallModelCollection(CtBlock<?> body) {

			return body.filterChildren((CtInvocation inv) -> {
				if (inv.getTarget() instanceof CtFieldRead) {
					CtFieldRead fielRead = (CtFieldRead) inv.getTarget();
					if (isModelCollection(fielRead.getType())) {
						//it is invocation on ModelList, ModelSet or ModelMap
						return true;
					}
				}
				return false;
			}).first() != null;
		}

		private boolean isModelCollection(CtTypeReference<?> typeRef) {
			Factory f = typeRef.getFactory();
			if (typeRef.isSubtypeOf(f.Type().createReference(ModelList.class))) {
				return true;
			}
			if (typeRef.isSubtypeOf(f.Type().createReference(ModelSet.class))) {
				return true;
			}
			return false;
		}

		private boolean hasPushToStackInvocation(CtBlock<?> body) {
			return !body.getElements(new TypeFilter<CtInvocation<?>>(CtInvocation.class) {
				@Override
				public boolean matches(CtInvocation<?> element) {
					return FineModelChangeListener.class.getSimpleName().equals(element.getExecutable().getDeclaringType().getSimpleName()) && super.matches(element);
				}
			}).isEmpty();
		}

		private void process(CtMethod<?> element) {
			count++;
			result += element.getSignature() + " on " + element.getDeclaringType().getQualifiedName() + "\n";
		}

		@Override
		public <T> void visitCtMethod(CtMethod<T> m) {
			if (isToBeProcessed(m)) {
				process(m);
			}
			super.visitCtMethod(m);
		}
	}
}
