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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import spoon.SpoonException;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExpression;
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

/**
 */
@Experimental
abstract class AbstractCompilationUnitImportsProcessor<T extends CtScanner, U> extends AbstractProcessor<CtCompilationUnit> {

	@Override
	public void process(CtCompilationUnit cu) {
		process(createScanner(), cu);
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

	protected abstract T createRawScanner();

	protected CtScannerListener createScannerListener(T scanner) {
		return new ScannerListener(scanner);
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

	protected class ScannerListener implements CtScannerListener {
		protected T scanner;
		protected Set<CtRole> ignoredRoles = IGNORED_ROLES_WHEN_IMPLICIT;

		ScannerListener(T scanner) {
			super();
			this.scanner = scanner;
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
						//continue. This is relevant for import
					} else {
						//May be this can never happen
						throw new SpoonException("Check this case. Is it relvant or not?");
					}
				}
				if (role == CtRole.TYPE && element instanceof CtTypeReference) {
					if (parent instanceof CtFieldReference) {
						//ignore the type of field references. It is not relevant for Imports
						return ScanningMode.SKIP_ALL;
					}
					if (parent instanceof CtExecutableReference) {
						CtElement parent2 = null;
						if (element.isParentInitialized()) {
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
			onEnter(getContext(scanner), role, element);
			return ScanningMode.NORMAL;
		}
	}

	protected abstract U getContext(T scanner);

	protected T createScanner() {
		T scanner = createRawScanner();
		if (scanner instanceof EarlyTerminatingScanner) {
			CtScannerListener listener = createScannerListener(scanner);
			if (listener != null) {
				((EarlyTerminatingScanner) scanner).setListener(listener);
			}
		}
		return scanner;
	}

	protected void onEnter(U context, CtRole role, CtElement element) {

		if (element instanceof CtTargetedExpression) {
			CtTargetedExpression<?, ?> targetedExpression = (CtTargetedExpression<?, ?>) element;
			CtExpression<?> target = targetedExpression.getTarget();
			if (target == null) {
				return;
			}
			handleTargetedExpression(context, role, targetedExpression, target);
		} else if (element instanceof CtTypeReference<?>) {
			//we have to visit only PURE CtTypeReference. No CtArrayTypeReference, CtTypeParameterReference, ...
			element.accept(new CtAbstractVisitor() {
				@Override
				public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
					handleTypeReference(context, role, (CtTypeReference<?>) element);
				}
			});
		}
	}

	protected abstract void handleTypeReference(U context, CtRole role, CtTypeReference<?> element);

	protected abstract void handleTargetedExpression(U context, CtRole role, CtTargetedExpression<?, ?> targetedExpression, CtExpression<?> target);

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
