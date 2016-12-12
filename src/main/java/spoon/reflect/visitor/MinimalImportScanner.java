/**
 * Copyright (C) 2006-2016 INRIA and contributors
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

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.declaration.CtClassImpl;

import java.util.HashSet;
import java.util.Set;

/**
 * A scanner dedicated to import only the necessary packages, @see spoon.test.variable.AccessFullyQualifiedTest
 *
 */
public class MinimalImportScanner extends ImportScannerImpl implements ImportScanner {

	private Set<String> namedElements = new HashSet<String>();

	private CtClass getParentClass(CtReference ref) {
		CtElement parent = ref.getParent();

		while (parent != null && !(parent instanceof CtClass)) {
			parent = parent.getParent();
		}

		if (parent == null) {
			return null;
		} else {
			return (CtClass)parent;
		}
	}

	/**
	 * Test if the reference should be imported by looking if there is a name conflict
	 * @param ref
	 * @return true if the ref should be imported.
	 */
	private boolean shouldTypeBeImported(CtReference ref) {
		// we import the targetType by default to simplify and avoid conclict in inner classes
		if (ref.equals(targetType)) {
			return true;
		}

		try {
			CtElement parent;
			if (ref instanceof CtTypeReference) {
				parent = ref.getParent();
			} else {
				parent = ref;
			}

			CtClass parentClass = this.getParentClass(ref);

			if (parent instanceof CtNamedElement) {
				CtNamedElement namedElement = (CtNamedElement)parent;

				if (parentClass != null && parentClass.getReference() != null) {
					if (parentClass.getReference().equals(targetType)) {
						namedElements.add(namedElement.getSimpleName());
					}
				} else {
					namedElements.add(namedElement.getSimpleName());
				}
			}

			while (!(parent instanceof CtPackage)) {
				/*if (parent instanceof CtClassImpl) {
					CtClassImpl classParent = (CtClassImpl)parent;
					CtTypeReference referencedType = classParent.getReference();

					if (referencedType != null && referencedType.equals(this.targetType)) {
						return false;
					}

				}*/
				if ((parent instanceof CtFieldReference) || (parent instanceof CtExecutableReference)) {
					CtReference parentType = (CtReference) parent;
					Set<String> qualifiedNameTokens = new HashSet<>();

					qualifiedNameTokens.add(parentType.getSimpleName());

					CtTypeReference typeReference;
					if (parent instanceof CtFieldReference) {
						typeReference = ((CtFieldReference)parent).getDeclaringType();
					} else {
						typeReference = ((CtExecutableReference)parent).getDeclaringType();
					}

					if (typeReference != null) {
						qualifiedNameTokens.add(typeReference.getSimpleName());

						if (typeReference.getPackage() != null) {
							CtPackage ctPackage = typeReference.getPackage().getDeclaration();

							while (ctPackage != null) {
								qualifiedNameTokens.add(ctPackage.getSimpleName());

								CtElement packParent = ctPackage.getParent();
								if (packParent.getParent() != null) {
									ctPackage = (CtPackage) packParent;
								} else {
									ctPackage = null;
								}
							}
						}
					}
					for (String token : qualifiedNameTokens) {
						if (namedElements.contains(token)) {
							return true;
						}
					}

				}
				parent = parent.getParent();
			}
		} catch (ParentNotInitializedException e) {
			return false;
		}

		return false;
	}

	@Override
	protected boolean addClassImport(CtTypeReference<?> ref) {
		boolean shouldTypeBeImported = this.shouldTypeBeImported(ref);

		if (shouldTypeBeImported) {
			return super.addClassImport(ref);
		} else {
			return false;
		}
	}

	@Override
	protected boolean addFieldImport(CtFieldReference ref) {
		boolean shouldTypeBeImported = this.shouldTypeBeImported(ref);

		if (shouldTypeBeImported) {
			return super.addFieldImport(ref);
		} else {
			return false;
		}
	}

	@Override
	protected boolean addMethodImport(CtExecutableReference ref) {
		boolean shouldTypeBeImported = this.shouldTypeBeImported(ref);

		if (shouldTypeBeImported) {
			return super.addMethodImport(ref);
		} else {
			return false;
		}
	}
}
