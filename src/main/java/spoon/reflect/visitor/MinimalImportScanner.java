/**
 * Copyright (C) 2006-2017 INRIA and contributors
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

import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A scanner dedicated to import only the necessary packages, @see spoon.test.variable.AccessFullyQualifiedTest
 *
 */
public class MinimalImportScanner extends ImportScannerImpl implements ImportScanner {

	private Set<String> fieldAndMethodsNames = new HashSet<String>();

	private CtClass getParentClass(CtReference ref) {
		CtElement parent = ref.getParent();

		while (parent != null && !(parent instanceof CtClass)) {
			parent = parent.getParent();
		}

		if (parent == null) {
			return null;
		} else {
			return (CtClass) parent;
		}
	}

	private Set<String> lookForLocalVariables(CtElement parent) {
		Set<String> result = new HashSet<>();

		// try to get the block container
		// if the first container is the class, then we are not in a block and we can quit now.
		while (parent != null && !(parent instanceof CtBlock)) {
			if (parent instanceof CtClass) {
				return result;
			}
			parent = parent.getParent();
		}

		if (parent != null) {
			CtBlock block = (CtBlock) parent;
			boolean innerClass = false;

			// now we have the first container block, we want to check if we're not in an inner class
			while (parent != null && !(parent instanceof CtClass)) {
				parent = parent.getParent();
			}

			if (parent != null) {
				// uhoh it's not a package as a parent, we must in an inner block:
				// let's find the last block BEFORE the class call: some collision could occur because of variables defined in that block
				if (!(parent.getParent() instanceof CtPackage)) {
					while (parent != null && !(parent instanceof CtBlock)) {
						parent = parent.getParent();
					}

					if (parent != null) {
						block = (CtBlock) parent;
					}
				}
			}

			AccessibleVariablesFinder avf = new AccessibleVariablesFinder(block);
			List<CtVariable> variables = avf.find();

			for (CtVariable variable : variables) {
				result.add(variable.getSimpleName());
			}
		}

		return result;
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

			Set<String> localVariablesOfBlock = new HashSet<>();

			if (parent instanceof CtField) {
				this.fieldAndMethodsNames.add(((CtField) parent).getSimpleName());
			} else if (parent instanceof CtMethod) {
				this.fieldAndMethodsNames.add(((CtMethod) parent).getSimpleName());
			} else {
				localVariablesOfBlock = this.lookForLocalVariables(parent);
			}

			while (!(parent instanceof CtPackage)) {
				if ((parent instanceof CtFieldReference) || (parent instanceof CtExecutableReference)) {
					CtReference parentType = (CtReference) parent;
					LinkedList<String> qualifiedNameTokens = new LinkedList<>();

					// we don't want to test the current ref name, as we risk to create field import and make autoreference
					if (parentType != parent) {
						qualifiedNameTokens.add(parentType.getSimpleName());
					}

					CtTypeReference typeReference;
					if (parent instanceof CtFieldReference) {
						typeReference = ((CtFieldReference) parent).getDeclaringType();
					} else {
						typeReference = ((CtExecutableReference) parent).getDeclaringType();
					}

					if (typeReference != null) {
						qualifiedNameTokens.add(typeReference.getSimpleName());

						if (typeReference.getPackage() != null) {
							CtPackage ctPackage = typeReference.getPackage().getDeclaration();

							while (ctPackage != null) {
								qualifiedNameTokens.add(ctPackage.getSimpleName());

								CtElement packParent = ctPackage.getParent();
								if (packParent.getParent() != null && !((CtPackage) packParent).getSimpleName().equals(CtPackage.TOP_LEVEL_PACKAGE_NAME)) {
									ctPackage = (CtPackage) packParent;
								} else {
									ctPackage = null;
								}
							}
						}
					}
					if (!qualifiedNameTokens.isEmpty()) {
						if (fieldAndMethodsNames.contains(qualifiedNameTokens.getLast()) || localVariablesOfBlock.contains(qualifiedNameTokens.getLast())) {
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
			if (this.fieldImports.containsKey(ref.getSimpleName())) {
				return isImportedInFieldImports(ref);
			}

			fieldImports.put(ref.getSimpleName(), ref);
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean addMethodImport(CtExecutableReference ref) {
		boolean shouldTypeBeImported = this.shouldTypeBeImported(ref);

		if (shouldTypeBeImported) {
			if (this.methodImports.containsKey(ref.getSimpleName())) {
				return isImportedInMethodImports(ref);
			}

			methodImports.put(ref.getSimpleName(), ref);

			if (ref.getDeclaringType() != null) {
				if (ref.getDeclaringType().getPackage() != null) {
					if (ref.getDeclaringType().getPackage().equals(this.targetType.getPackage())) {
						addClassImport(ref.getDeclaringType());
					}
				}
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean isImportedInClassImports(CtTypeReference<?> ref) {
		if (!(ref.isImplicit()) && classImports.containsKey(ref.getSimpleName())) {
			CtTypeReference<?> exist = classImports.get(ref.getSimpleName());
			if (exist.getQualifiedName().equals(ref.getQualifiedName())) {
				return true;
			}
		}
		return false;
	}
}
