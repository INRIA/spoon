/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import spoon.processing.AbstractProcessor;
import spoon.processing.Processor;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtTargetedExpression;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.chain.CtScannerListener;
import spoon.reflect.visitor.chain.ScanningMode;
import spoon.support.Experimental;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *{@link Processor} of {@link CtCompilationUnit}, which scans CtCompilationUnit modules, packages and types
 * with purpose to find type references and expressions which might influence import directives.
 *
 * Subclasses create a scanner ({@link #createScanner()}) and analyzes the elements to be imported {@link #handleTypeReference} and {@link #handleTargetedExpression(CtTargetedExpression, Object)}
 *
 */
@Experimental
abstract class ImportAnalyzer<U> extends AbstractProcessor<CtElement> {

	protected EarlyTerminatingScanner scanner;
	@Override
	public void process(CtElement el) {
		scanner = createScanner();
		CtScannerListener listener = createScannerListener();
		scanner.setListener(listener);
		if (el instanceof CtCompilationUnit) {
			process(scanner, (CtCompilationUnit) el);
		} else {
			scanner.scan(el);
		}
	}

	protected static void process(CtScanner scanner, CtCompilationUnit cu) {
		scanner.enter(cu);
		switch (cu.getUnitType()) {
		case MODULE_DECLARATION:
		case UNKNOWN:
			break;
		case PACKAGE_DECLARATION:
			// we need to compute imports only for package annotations and package comments
			// we don't want to get all imports coming from content of package
			CtPackage pack = cu.getDeclaredPackage();
			scanner.scan(pack.getAnnotations());
			break;
		case TYPE_DECLARATION:
			for (CtTypeReference<?> typeRef : cu.getDeclaredTypeReferences()) {
				scanner.scan(typeRef.getTypeDeclaration());
			}
			break;
		}
		scanner.exit(cu);
	}

	protected CtScannerListener createScannerListener() {
		return new ScannerListener();
	}

	//The set of roles whose values are always kept implicit
	protected static Set<CtRole> IGNORED_ROLES_WHEN_IMPLICIT = new HashSet<>(Arrays.asList(
			//e.g. List<String> s = new ArrayList</*keep me implicit*/>();
			CtRole.TYPE_ARGUMENT,
			//e.g. List<?/* extends Object*/>
			CtRole.BOUNDING_TYPE,
			//e.g. (/*implicit type of parameter*/ p) -> {}
			CtRole.TYPE
	));

	/**
	 * {@link CtScannerListener} implementation which stops scanning of children on elements,
	 * which mustn't have influence to compilation unit imports.
	 */
	protected class ScannerListener implements CtScannerListener {
		protected Set<CtRole> ignoredRoles = IGNORED_ROLES_WHEN_IMPLICIT;

		ScannerListener() {
			super();
		}

		@Override
		public ScanningMode enter(CtRole role, CtElement element) {
			if (element == null) {
				return ScanningMode.SKIP_ALL;
			}
			if (role == CtRole.VARIABLE && element instanceof CtVariableReference) {
				//ignore variable reference of field access. The accessType is relevant here instead.
				return ScanningMode.SKIP_ALL;
			}
			if (element.isParentInitialized()) {
				CtElement parent = element.getParent();
				if (role == CtRole.DECLARING_TYPE && element instanceof CtTypeReference) {
					if (parent instanceof CtFieldReference) {
						//ignore the declaring type of field reference. It is not relevant for Imports
						return ScanningMode.SKIP_ALL;
					}
					if (parent instanceof CtExecutableReference) {
						/*
						 * ignore the declaring type of type executable like
						 * anVariable.getSomeInstance().callMethod()
						 * The declaring type of `callMethod` method is not relevant for Imports
						 */
						return ScanningMode.SKIP_ALL;
					} else if (parent instanceof CtTypeReference) {
						/*
						 * It looks like this is not needed too.
						 *
						 * pvojtechovsky: I am sure it is not wanted in case of
						 * spoon.test.imports.testclasses.internal.ChildClass.InnerClassProtected
						 * which extends package protected (and for others invisible class)
						 * spoon.test.imports.testclasses.internal.SuperClass
						 * and in this case the import directive must import ...ChildClass and not ...SuperClass,
						 * because import is using type "access path" and not qualified name of the type.
						 *
						 * ... but in other normal cases, I guess the declaring type is used and needed for import!
						 * ... so I don't understand why SKIP_ALL works in all cases. May be there is missing test case?
						 */
						if (!((CtTypeReference) parent).getAccessType().equals(element)) {
							return ScanningMode.SKIP_ALL;
						}
					}
				}
				if (role == CtRole.TYPE && element instanceof CtTypeReference) {
					if (parent instanceof CtFieldReference) {
						//ignore the type of field references. It is not relevant for Imports
						return ScanningMode.SKIP_ALL;
					}
					if (parent instanceof CtExecutableReference) {
						CtElement parent2 = null;
						if (parent.isParentInitialized()) {
							parent2 = parent.getParent();
						}
						if (parent2 instanceof CtConstructorCall<?>) {
							//new SomeType(); is relevant for import
							//continue
						} else {
							/*
							 * ignore the return type of executable reference. It is not relevant for Imports
							 * anVariable.getSomeInstance().callMethod()
							 * The return type `callMethod` method is not relevant for Imports
							 */
							return ScanningMode.SKIP_ALL;
						}
					}
					/*
					 * CtTypeReference
					 * CtMethod, CtField, ...
					 * continue. This is relevant for import
					 */
				}
				if (role == CtRole.ARGUMENT_TYPE) {
					/*
					 * ignore the type of parameter of CtExecutableReference
					 * It is not relevant for Imports.
					 */
					return ScanningMode.SKIP_ALL;
				}
			}
			if (element.isImplicit() && ignoredRoles.contains(role)) {
				//ignore implicit actual type arguments
				return ScanningMode.SKIP_ALL;
			}
			onEnter(getScannerContextInformation(), role, element);
			return ScanningMode.NORMAL;
		}
	}


	protected void onEnter(U context, CtRole role, CtElement element) {

		if (element instanceof CtTargetedExpression) {
			CtTargetedExpression<?, ?> targetedExpression = (CtTargetedExpression<?, ?>) element;
			handleTargetedExpression(targetedExpression, context);
		} else if (element instanceof CtTypeReference<?>) {
			//we have to visit only PURE CtTypeReference. No CtArrayTypeReference, CtTypeParameterReference, ...
			element.accept(new CtAbstractVisitor() {
				@Override
				public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
					handleTypeReference((CtTypeReference<?>) element, context, role);
				}
			});
		}
	}

	/** extract the required information from the scanner to take a decision */
	protected abstract U getScannerContextInformation();

	/** creates the scanner that will be used to visit the model */
	protected abstract EarlyTerminatingScanner createScanner();

	/** what do we do a type reference? */
	protected abstract void handleTypeReference(CtTypeReference<?> element, U context, CtRole role);

	/** what do we do a target expression (print target or not) ? */
	protected abstract void handleTargetedExpression(CtTargetedExpression<?, ?> targetedExpression, U context);

	/**
	 * @return parent of `element`, but only if it's type is `type`
	 */
	protected static <T extends CtElement> T getParentIfType(CtElement element, Class<T> type) {
		if (element == null || !element.isParentInitialized()) {
			return null;
		}
		CtElement parent = element.getParent();
		if (type.isInstance(parent)) {
			return type.cast(parent);
		}
		return null;
	}
}
