/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon;


import spoon.reflect.CtModelImpl;
import spoon.reflect.code.CtArrayWrite;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtShadowable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.path.CtPath;
import spoon.reflect.path.CtPathException;
import spoon.reflect.path.CtPathStringBuilder;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtBiScannerDefault;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.JavaIdentifiers;
import spoon.reflect.visitor.PrinterHelper;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.Experimental;
import spoon.support.Internal;
import spoon.support.reflect.CtExtendedModifier;
import spoon.support.sniper.internal.ElementSourceFragment;
import spoon.support.visitor.equals.EqualsVisitor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import static spoon.testing.utils.Check.assertNotNull;

/**
 * Verifies all contracts that should hold on any AST.
 *
 * Usage: `new ContractVerifier(pack).verifyAll();`
 */
@Experimental
public class ContractVerifier {

	private CtPackage _rootPackage;

	public ContractVerifier(CtPackage rootPackage) {
		this._rootPackage = rootPackage;
	}

	/** use at your own risk, not part of the public API */
	public ContractVerifier() {
	}


	/** verify all possible contracts in this class */
	public void verify() {
		checkShadow();
		checkParentContract();
		checkParentConsistency();
		checkModifiers();
		checkAssignmentContracts();
		checkContractCtScanner();
		checkBoundAndUnboundTypeReference();
		checkModelIsTree();
		checkContractCtScanner();
		checkElementIsContainedInAttributeOfItsParent();
		checkElementToPathToElementEquivalence();
		checkRoleInParent();
		checkJavaIdentifiers();
	}

	/** verifies that the explicit modifier should be present in the original source code */
	public void checkModifiers() {
		for (CtModifiable modifiable : _rootPackage.getElements(new TypeFilter<>(CtModifiable.class))) {
			for (CtExtendedModifier modifier : modifiable.getExtendedModifiers()) {
				if (modifier.isImplicit()) {
					continue;
				}
				SourcePosition position = modifier.getPosition();
				CompilationUnit compilationUnit = position.getCompilationUnit();
				String originalSourceCode = compilationUnit.getOriginalSourceCode();
				assertEquals(modifier.getKind().toString(), originalSourceCode.substring(position.getSourceStart(), position.getSourceEnd() + 1));
			}
		}
	}

	private static void assertTrue(String msg, boolean conditionThatMustHold) {
		if (!conditionThatMustHold) {
			throw new AssertionError(msg);
		}
	}

	private void assertFalse(boolean condition) {
		assertTrue("", !condition);
	}


	private void assertEquals(Object expected, Object actual) {
		assertEquals("assertEquals violation", expected, actual);
	}


	private void assertEquals(String msg, Object expected, Object actual) {
		if (!expected.equals(actual)) {
			throw new AssertionError(msg);
		}
	}

	private void assertNotSame(Object element, Object other) {
		if (element == other) {
			throw new AssertionError("assertSame violation");
		}
	}

	private void assertSame(Object element, Object other) {
		assertSame("assertSame violation", element, other);
	}

	private void assertSame(String msg, Object element, Object other) {
		if (element != other) {
			throw new AssertionError(msg);
		}
	}

	private void fail(String msg) {
		throw new AssertionError(msg);
	}

	/** checks that there is always one parent, corresponding to the scanning order */
	public void checkParentContract() {
		_rootPackage.filterChildren(null).forEach((CtElement elem) -> {
			// there is always one parent
			assertTrue("no parent for " + elem.getClass() + "-" + elem.getPosition(), elem.isParentInitialized());
		});

		// the scanner and the parent are in correspondence
		new CtScanner() {
			Deque<CtElement> elementStack = new ArrayDeque<>();

			@Override
			public void scan(CtElement e) {
				if (e == null) {
					return;
				}
				if (e instanceof CtReference) {
					return;
				}
				if (!elementStack.isEmpty()) {
					assertEquals(elementStack.peek(), e.getParent());
				}
				elementStack.push(e);
				e.accept(this);
				elementStack.pop();
			}
		}.scan(_rootPackage);

	}


	public void checkBoundAndUnboundTypeReference() {
		new CtScanner() {
			@Override
			public void visitCtTypeParameterReference(CtTypeParameterReference ref) {
				CtTypeParameter declaration = ref.getDeclaration();
				if (declaration != null) {
					assertEquals(ref.getSimpleName(), declaration.getSimpleName());
				}
				super.visitCtTypeParameterReference(ref);
			}
		}.scan(_rootPackage);
	}

	/** check that we have all shadow elements, and that they are correctly isShadow */
	public void checkShadow() {
		new CtScanner() {
			@Override
			public void scan(CtElement element) {
				if (element != null && CtShadowable.class.isAssignableFrom(element.getClass())) {
					assertFalse(((CtShadowable) element).isShadow());
				}
				super.scan(element);
			}

			@Override
			public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
				assertNotNull(reference);
				if (CtTypeReference.NULL_TYPE_NAME.equals(reference.getSimpleName()) || "?".equals(reference.getSimpleName())) {
					super.visitCtTypeReference(reference);
					return;
				}
				final CtType<T> typeDeclaration = reference.getTypeDeclaration();
				assertNotNull(reference.toString() + " cannot be found in ", typeDeclaration);
				assertEquals(reference.getSimpleName(), typeDeclaration.getSimpleName());
				assertEquals(reference.getQualifiedName(), typeDeclaration.getQualifiedName());

				if (reference.getDeclaration() == null) {
					assertTrue("typeDeclaration must be shadow", typeDeclaration.isShadow());
				}
				super.visitCtTypeReference(reference);
			}

			@Override
			public <T> void visitCtExecutableReference(CtExecutableReference<T> reference) {
				super.visitCtExecutableReference(reference);
				assertNotNull(reference);
				if (isLanguageExecutable(reference)) {
					return;
				}
				final CtExecutable<T> executableDeclaration = reference.getExecutableDeclaration();
				assertNotNull("cannot find decl for " + reference.toString(), executableDeclaration);
				assertEquals(reference.getSimpleName(), executableDeclaration.getSimpleName());

				// when a generic type is used in a parameter and return type, the shadow type doesn't have these information.
				for (int i = 0; i < reference.getParameters().size(); i++) {
					//TODO assertions which are checking lambdas. Till then ignore lambdas.
					if (executableDeclaration instanceof CtLambda) {
						return;
					}
					CtTypeReference<?> methodParamTypeRef = executableDeclaration.getParameters().get(i).getType();
					assertEquals(reference.getParameters().get(i).getQualifiedName(), methodParamTypeRef.getTypeErasure().getQualifiedName());
				}

				// contract: the reference and method signature are the same
				if (reference.getActualTypeArguments().isEmpty()
						&& executableDeclaration instanceof CtMethod
						&& !((CtMethod) executableDeclaration).getFormalCtTypeParameters().isEmpty()
						) {
					assertEquals(reference.getSignature(), executableDeclaration.getSignature());
				}

				// contract: the reference and constructor signature are the same
				if (reference.getActualTypeArguments().isEmpty()
						&& executableDeclaration instanceof CtConstructor
						&& !((CtConstructor) executableDeclaration).getFormalCtTypeParameters().isEmpty()
						) {
					assertEquals(reference.getSignature(), executableDeclaration.getSignature());
				}

				if (reference.getDeclaration() == null && CtShadowable.class.isAssignableFrom(executableDeclaration.getClass())) {
					assertTrue("execDecl at " + reference.toString() + " must be shadow ", ((CtShadowable) executableDeclaration).isShadow());
				}

			}

			private <T> boolean isLanguageExecutable(CtExecutableReference<T> reference) {
				return "values".equals(reference.getSimpleName());
			}

			@Override
			public <T> void visitCtFieldReference(CtFieldReference<T> reference) {
				assertNotNull(reference);
				if (isLanguageField(reference) || isDeclaredInSuperClass(reference)) {
					super.visitCtFieldReference(reference);
					return;
				}
				final CtField<T> fieldDeclaration = reference.getFieldDeclaration();
				assertNotNull(fieldDeclaration);
				assertEquals(reference.getSimpleName(), fieldDeclaration.getSimpleName());
				assertEquals(reference.getType().getQualifiedName(), fieldDeclaration.getType().getQualifiedName());

				if (reference.getDeclaration() == null) {
					assertTrue("fieldDecl must be shadow", fieldDeclaration.isShadow());
				}
				super.visitCtFieldReference(reference);
			}

			private <T> boolean isLanguageField(CtFieldReference<T> reference) {
				return "class".equals(reference.getSimpleName()) || "length".equals(reference.getSimpleName());
			}

			private <T> boolean isDeclaredInSuperClass(CtFieldReference<T> reference) {
				final CtType<?> typeDeclaration = reference.getDeclaringType().getTypeDeclaration();
				return typeDeclaration != null && typeDeclaration.getField(reference.getSimpleName()) == null;
			}
		}.visitCtPackage(_rootPackage);
	}

	/** verifies the core scanning contracts (enter implies exit, etc) */
	public void checkContractCtScanner() {
		class Counter {
			int scan;
			int enter;
			int exit;
		}

		final Counter counter = new Counter();
		final Counter counterInclNull = new Counter();

		new CtScanner() {

			@Override
			public void scan(CtElement element) {
				counterInclNull.scan++;
				if (element != null) {
					counter.scan++;
				}
				super.scan(element);
			}

			@Override
			public void enter(CtElement element) {
				counter.enter++;
				super.enter(element);
			}

			@Override
			public void exit(CtElement element) {
				counter.exit++;
				super.exit(element);
			}

		}.scan(_rootPackage);

		assertTrue("violated contract: when enter is called, exit is also called", counter.enter == counter.exit);

		assertTrue(" violated contract: all scanned elements ust call enter", counter.enter == counter.scan);

		Counter counterBiScan = new Counter();
		class ActualCounterScanner extends CtBiScannerDefault {
			@Override
			public void biScan(CtElement element, CtElement other) {
				super.biScan(element, other);
				counterBiScan.scan++;
				if (element == null) {
					if (other != null) {
						fail("element can't be null if other isn't null.");
					}
				} else if (other == null) {
					fail("other can't be null if element isn't null.");
				} else {
					// contract: all elements have been cloned and are still equal
					EqualsVisitor ev = new EqualsVisitor();
					boolean res = ev.checkEquals(element, other);
					Object notEqualOther = ev.getNotEqualOther();
					String pb = "";
					if (notEqualOther != null) {
						notEqualOther.toString();
					}
					if (notEqualOther instanceof CtElement) {
						pb += " " + ((CtElement) notEqualOther).getPosition().toString();
					}
					assertTrue("not equal: " + pb, res);

					assertNotSame(element, other);
				}
			}
		}
		final ActualCounterScanner actual = new ActualCounterScanner();
		actual.biScan(_rootPackage, _rootPackage.clone());

		// contract: scan and biscan are executed the same number of times
		assertEquals(counterInclNull.scan, counterBiScan.scan);

		// for pure beauty: parallel visit of the same tree!
		Counter counterBiScan2 = new Counter();
		new CtBiScannerDefault() {
			@Override
			public void biScan(CtElement element, CtElement other) {
				counterBiScan2.scan++;
				// we have the exact same element
				assertSame(element, other);
				super.biScan(element, other);
			}
		}.biScan(_rootPackage, _rootPackage);
		// contract: scan and biscan are executed the same number of times
		assertEquals(counterInclNull.scan, counterBiScan2.scan);
	}

	/** checks that all assignments are aither a CtFieldWrite, a CtVariableWrite or a CtArrayWrite */
	public void checkAssignmentContracts() {
		for (CtAssignment assign : _rootPackage.getElements(new TypeFilter<>(CtAssignment.class))) {
			CtExpression assigned = assign.getAssigned();
			if (!(assigned instanceof CtFieldWrite
					|| assigned instanceof CtVariableWrite || assigned instanceof CtArrayWrite)) {
				throw new AssertionError("AssignmentContract error:" + assign.getPosition() + "\n" + assign.toString() + "\nAssigned is " + assigned.getClass());
			}
		}
	}

	/** checks that the scanner behavior and the parents correspond */
	public void checkParentConsistency() {
		checkParentConsistency(_rootPackage);
	}

	/** public modifier for testing purpose only, not in the public API */
	@Internal
	public void checkParentConsistency(CtElement element) {
		final Set<CtElement> inconsistentParents = new HashSet<>();
		new CtScanner() {
			private Deque<CtElement> previous = new ArrayDeque();

			@Override
			protected void enter(CtElement e) {
				if (e != null) {
					if (!previous.isEmpty()) {
						try {
							if (e.getParent() != previous.getLast()) {
								inconsistentParents.add(e);
							}
						} catch (ParentNotInitializedException ignore) {
							inconsistentParents.add(e);
						}
					}
					previous.add(e);
				}
				super.enter(e);
			}

			@Override
			protected void exit(CtElement e) {
				if (e == null) {
					return;
				}
				if (e.equals(previous.getLast())) {
					previous.removeLast();
				} else {
					throw new RuntimeException("Inconsistent stack");
				}
				super.exit(e);
			}
		}.scan(element);
		assertEquals("All parents have to be consistent", 0, inconsistentParents.size());
	}

	/**
	 * contract: each element is used only once in the model
	 */
	public void checkModelIsTree() {
		Exception dummyException = new Exception("STACK");
		PrinterHelper problems = new PrinterHelper(_rootPackage.getFactory().getEnvironment());
		Map<CtElement, Exception> allElements = new IdentityHashMap<>();
		_rootPackage.filterChildren(null).forEach((CtElement ele) -> {
			//uncomment this line to get stacktrace of real problem. The dummyException is used to avoid OutOfMemoryException
//			Exception secondStack = new Exception("STACK");
			Exception secondStack = dummyException;
			Exception firstStack = allElements.put(ele, secondStack);
			if (firstStack != null) {
				if (firstStack == dummyException) {
					fail("The Spoon model is not a tree. The " + ele.getClass().getSimpleName() + ":" + ele.toString() + " is shared");
				}
				//the element ele was already visited. It means it used on more places
				//report the stacktrace of first and second usage, so that place can be found easily
				problems.write("The element " + ele.getClass().getSimpleName()).writeln()
						.incTab()
						.write(ele.toString()).writeln()
						.write("Is linked by these stacktraces").writeln()
						.write("1) " + getStackTrace(firstStack)).writeln()
						.write("2) " + getStackTrace(secondStack)).writeln()
						.decTab();
			}
		});

		String report = problems.toString();
		if (!report.isEmpty()) {
			fail(report);
		}
	}

	private String getStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}

	public void checkRoleInParent() {
		_rootPackage.accept(new CtScanner() {
			@Override
			public void scan(CtRole role, CtElement element) {
				if (element != null) {
					//contract: getMyRoleInParent returns the expected parent
					assertSame(role, element.getRoleInParent());
				}
				super.scan(role, element);
			}
		});
	}

	/**
	 * Asserts that all siblings and children of sp are well ordered
	 * @param sourceFragment
	 * @param minOffset TODO
	 * @param maxOffset TODO
	 * @return number of checked {@link SourcePosition} nodes
	 */
	private int assertSourcePositionTreeIsCorrectlyOrder(ElementSourceFragment sourceFragment, int minOffset, int maxOffset) {
		int nr = 0;
		int pos = minOffset;
		while (sourceFragment != null) {
			nr++;
			assertTrue("min(" + pos + ") <= fragment.start(" + sourceFragment.getStart() + ")", pos <= sourceFragment.getStart());
			assertTrue("fragment.start(" + sourceFragment.getStart() + ") <= fragment.end(" + sourceFragment.getEnd() + ")", sourceFragment.getStart() <= sourceFragment.getEnd());
			pos = sourceFragment.getEnd();
			nr += assertSourcePositionTreeIsCorrectlyOrder(sourceFragment.getFirstChild(), sourceFragment.getStart(), sourceFragment.getEnd());
			sourceFragment = sourceFragment.getNextSibling();
		}
		assertTrue("lastFragment.end(" + pos + ") <= max(" + maxOffset + ")", pos <= maxOffset);
		return nr;
	}

	/** checks that for all elements, the path can be obtained, parsed, and give the same element when evaluated */
	public void checkElementToPathToElementEquivalence() {
		_rootPackage.getPackage("spoon").getElements(e -> true).parallelStream().forEach(element -> {
			CtPath path = element.getPath();
			String pathStr = path.toString();
			try {
				CtPath pathRead = new CtPathStringBuilder().fromString(pathStr);
				assertEquals(pathStr, pathRead.toString());
				Collection<CtElement> returnedElements = pathRead.evaluateOn(_rootPackage);
				//contract: CtUniqueRolePathElement.evaluateOn() returns a unique elements if provided only a list of one inputs
				assertEquals(1, returnedElements.size());
				CtElement actualElement = (CtElement) returnedElements.toArray()[0];
				//contract: Element -> Path -> String -> Path -> Element leads to the original element
				assertSame(element, actualElement);
			} catch (CtPathException e) {
				throw new AssertionError("Path " + pathStr + " is either incorrectly generated or incorrectly read", e);
			} catch (AssertionError e) {
				throw new AssertionError("Path " + pathStr + " detection failed on " + element.getClass().getSimpleName() + ": " + element.toString(), e);
			}
		});
	}

	/** contract: element is contained in attribute of element's parent */
	public void checkElementIsContainedInAttributeOfItsParent() {
		_rootPackage.accept(new CtScanner() {
			@Override
			public void scan(CtRole role, CtElement element) {
				if (element != null) {
					//contract: element is contained in attribute of element's parent
					CtElement parent = element.getParent();
					Object attributeOfParent = parent.getValueByRole(role);
					if (attributeOfParent instanceof CtElement) {
						assertSame("Element of type " + element.getClass().getName()
								+ " is not the value of attribute of role " + role.name()
								+ " of parent type " + parent.getClass().getName(), element, attributeOfParent);
					} else if (attributeOfParent instanceof Collection) {
						assertTrue("Element of type " + element.getClass().getName()
										+ " not found in Collection value of attribute of role " + role.name()
										+ " of parent type " + parent.getClass().getName(),
								((Collection<CtElement>) attributeOfParent).stream().anyMatch(e -> e == element));
					} else if (attributeOfParent instanceof Map) {
						assertTrue("Element of type " + element.getClass().getName()
										+ " not found in Map#values of attribute of role " + role.name()
										+ " of parent type " + parent.getClass().getName(),
								((Map<String, ?>) attributeOfParent).values().stream().anyMatch(e -> e == element));
					} else {
						fail("Attribute of Role " + role + " not checked");
					}
				}
				super.scan(role, element);
			}
		});
	}

	public void checkGenericContracts() {
		checkParentContract();

		// assignments
		checkAssignmentContracts();

		// scanners
		checkContractCtScanner();

		// type parameter reference.
		checkBoundAndUnboundTypeReference();
	}

	/** checks that the identifiers are valid */
	public void checkJavaIdentifiers() {
		// checking method JavaIdentifiers.isLegalJavaPackageIdentifier
		_rootPackage.getElements(new TypeFilter<>(CtPackage.class)).parallelStream().forEach(element -> {
			// the default package is excluded (called "unnamed package")
			if (element instanceof CtModelImpl.CtRootPackage) {
				return;
			}


			assertTrue("isLegalJavaPackageIdentifier is broken for " + element.getSimpleName() + " " + element.getPosition(), JavaIdentifiers.isLegalJavaPackageIdentifier(element.getSimpleName()));
		});
		// checking method JavaIdentifiers.isLegalJavaExecutableIdentifier
		_rootPackage.getElements(new TypeFilter<>(CtExecutable.class)).parallelStream().forEach(element -> {

			// static methods have an empty string as identifier
			if (element instanceof CtAnonymousExecutable) {
				return;
			}

			assertTrue("isLegalJavaExecutableIdentifier is broken " + element.getSimpleName() + " " + element.getPosition(), JavaIdentifiers.isLegalJavaExecutableIdentifier(element.getSimpleName()));
		});

	}
}
