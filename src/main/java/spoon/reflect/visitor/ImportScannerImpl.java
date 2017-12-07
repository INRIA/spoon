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

import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.declaration.CtElementImpl;
import spoon.support.reflect.reference.CtWildcardStaticTypeMemberReferenceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This scanner tries to optimize the imports in Spoon.
 */
public class ImportScannerImpl extends CtScanner implements ImportScanner {

	private Factory factory;
	private List<CtImport> imports = CtElementImpl.emptyList();
	private Set<CtImport> removedImports = CtElementImpl.emptySet();
	private Set<CtReference> referenceInCollision = CtElementImpl.emptySet();

	//top declaring type of that import
	protected CtTypeReference<?> targetType;

	public ImportScannerImpl(Factory factory) {
		this.factory = factory;
	}

	/**
	 * @deprecated Use constructor with factory parameter
	 */
	@Deprecated
	public ImportScannerImpl() {

	}

	@Override
	public void setFactory(Factory factory) {
		this.factory = factory;
	}

	@Override
	public Factory getFactory() {
		return factory;
	}

	@Override
	public void visitCtInvocation(CtInvocation invocation) {
		if (!this.isImported(invocation.getExecutable())) {
			super.visitCtInvocation(invocation);
		}
	}

	@Override
	public void visitCtFieldRead(CtFieldRead fieldRead) {
		if (fieldRead.getTarget() instanceof CtFieldRead) {
			this.visitCtFieldRead((CtFieldRead) fieldRead.getTarget());
		} else if (!this.isImported(fieldRead.getVariable())) {
			super.visitCtFieldRead(fieldRead);
		}
	}

	@Override
	public <T> void visitCtFieldReference(CtFieldReference<T> reference) {
		if (reference.isStatic()) {
			if (reference.getDeclaringType() != null && !isImported(reference.getDeclaringType())) {
				this.addImport(reference);
			}
		} else {
			super.visitCtFieldReference(reference);
		}
	}

	@Override
	public <T> void visitCtExecutableReference(CtExecutableReference<T> reference) {
		if (reference.isConstructor()) {
			super.visitCtExecutableReference(reference);
		} else if (reference.isStatic()) {
			if (reference.getDeclaringType() != null && !isImported(reference.getDeclaringType())) {
				this.addImport(reference);
			}
		}

		if (!reference.getActualTypeArguments().isEmpty()) {
			this.scan(reference.getActualTypeArguments());
		}
	}

	@Override
	public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
		if (!(reference instanceof CtArrayTypeReference) &&
				!reference.isPrimitive() &&
				!CtTypeReference.NULL_TYPE_NAME.equals(reference.getSimpleName())) {
			CtTypeReference typeReference;
			if (reference.getDeclaringType() == null) {
				typeReference = reference;
			} else {
				typeReference = reference.getTopLevelType();
			}

			this.addImport(typeReference);
		}
		super.visitCtTypeReference(reference);

	}

	@Override
	public void scan(CtElement element) {
		if (element != null && !element.isImplicit()) {
			element.accept(this);
		}
	}

	@Override
	public List<CtImport> getAllImports() {
		return Collections.unmodifiableList(this.imports);
	}

	@Override
	public void setImports(List<CtImport> importList) {
		if (importList != null && !importList.isEmpty()) {
			if (this.imports == CtElementImpl.<CtImport>emptyList()) {
				this.imports = new ArrayList<>();
			}

			this.imports.clear();
			for (CtImport ctImport : importList) {
				this.addImport(ctImport);
			}
		}
	}

	@Override
	public void addImport(CtImport ctImport) {
		if (ctImport != null) {
			if (this.imports == CtElementImpl.<CtImport>emptyList()) {
				this.imports = new ArrayList<>();
			}

			if (!this.imports.contains(ctImport)) {
				this.imports.add(ctImport);
			}
		}
	}

	protected void addImport(CtReference reference) {
		if (!isTypeInCollision(reference) && !isImported(reference) && isVisible(reference)) {
			CtImport ctImport = this.factory.Type().createImport(reference);

			if (!this.removedImports.contains(ctImport)) {
				this.addImport(ctImport);
			}
		}
	}

	protected boolean isTypeInCollision(CtReference reference) {

		// we have to check reference and target type using qualified name
		// and not simple equals, because the reference might contain type parameters.
		if (reference instanceof CtTypeReference) {
			CtTypeReference ctTypeReference = (CtTypeReference) reference;
			if (targetType != null && targetType.getQualifiedName().equals(ctTypeReference.getQualifiedName())) {
				return false;
			}
		}

		if (targetType != null && reference.getSimpleName().equals(targetType.getSimpleName())) {
			this.addReferenceInCollision(reference);
			return true;
		}

		return false;
	}

	protected boolean isVisible(CtReference reference) {
		if (reference instanceof CtTypeReference) {
			CtTypeReference ctTypeReference = (CtTypeReference) reference;
			if (ctTypeReference.isLocalType()) {
				return false;
			}
			if (!ctTypeReference.equals(ctTypeReference.getTopLevelType())) {
				ctTypeReference = ctTypeReference.getTopLevelType();
				if (isImported(ctTypeReference)) {
					return false;
				}
			}

			if (targetType == null) {
				return ctTypeReference.getModifiers().contains(ModifierKind.PUBLIC);
			}
			return targetType.canAccess(ctTypeReference);
		}

		return true;
	}

	@Override
	public void removeImport(CtImport ctImport) {
		if (this.removedImports == CtElementImpl.<CtImport>emptySet()) {
			this.removedImports = new HashSet<>();
		}

		this.removedImports.add(ctImport);
		this.imports.remove(ctImport);
	}

	@Override
	public void computeImports(CtElement element) {
		//look for top declaring type of that simpleType
		if (element instanceof CtType) {
			CtType simpleType = (CtType) element;
			targetType = simpleType.getReference().getTopLevelType();
			scan(simpleType);
		} else {
			CtType<?> type = element.getParent(CtType.class);
			targetType = type == null ? null : type.getReference().getTopLevelType();
			scan(element);
		}
	}

	@Override
	public void computeImports(CompilationUnit cu) {
		switch (cu.getUnitType()) {
			case TYPE_DECLARATION:
				for (CtType type : cu.getDeclaredTypes()) {
					this.targetType = type.getReference();
					this.scan(type);
				}
				break;

			case MODULE_DECLARATION:
				this.scan(cu.getDeclaredModule());
				break;

			case PACKAGE_DECLARATION:
				this.scan(cu.getDeclaredPackage().getAnnotations());
				break;
		}
	}

	@Override
	public boolean isEffectivelyImported(CtReference reference) {
		if (reference instanceof CtTypeReference) {
			CtTypeReference typeReference = (CtTypeReference) reference;

			if (typeReference.getPackage() != null) {
				// if the package is imported then the type is necessarily imported.
				if (this.imports.contains(this.factory.createImport(typeReference.getPackage()))) {
					return true;
				}
			}

		} else if (reference instanceof CtExecutableReference) {
			CtExecutableReference executableReference = (CtExecutableReference) reference;

			for (CtImport ctImport : this.imports) {
				if (ctImport.getReference() instanceof CtWildcardStaticTypeMemberReferenceImpl) {
					CtTypeReference ctWildcardStaticTypeMemberReference = ((CtWildcardStaticTypeMemberReferenceImpl) ctImport.getReference()).getOriginalTypeReference();
					if (ctWildcardStaticTypeMemberReference.getAllExecutables().contains(executableReference)) {
						return true;
					}
				}
			}
		} else if (reference instanceof CtFieldReference) {
			CtFieldReference fieldReference = (CtFieldReference) reference;

			for (CtImport ctImport : this.imports) {
				if (ctImport.getReference() instanceof CtWildcardStaticTypeMemberReferenceImpl) {
					CtTypeReference ctWildcardStaticTypeMemberReference = ((CtWildcardStaticTypeMemberReferenceImpl) ctImport.getReference()).getOriginalTypeReference();
					if (ctWildcardStaticTypeMemberReference.getAllFields().contains(fieldReference)) {
						return true;
					}
				}
			}
		}


		return this.imports.contains(this.factory.createImport(reference));
	}

	@Override
	public boolean isImported(CtReference ref) {
		if (this.isEffectivelyImported(ref)) {
			return true;
		}

		if (targetType != null && targetType.equals(ref)) {
			return true;
		}

		if (ref instanceof CtTypeReference) {
			CtTypeReference ctTypeReference = (CtTypeReference) ref;
			if (ctTypeReference.getPackage() != null && ((CtTypeReference) ref).getPackage().getSimpleName().equals("java.lang")) {
				return true;
			}

			if (targetType != null) {
				if (targetType.getPackage() != null && targetType.getPackage().equals(ctTypeReference.getPackage())) {
					return true;
				}
			}
		}
		return false;
	}

	protected void addReferenceInCollision(CtReference reference) {
		if (this.referenceInCollision == CtElementImpl.<CtReference>emptySet()) {
			this.referenceInCollision = new HashSet<>();
		}
		this.referenceInCollision.add(reference);
	}

	@Override
	public boolean printQualifiedName(CtReference ref) {
		return this.referenceInCollision.contains(ref);
	}

	/*private boolean isThereAnotherClassWithSameNameInAnotherPackage(CtTypeReference<?> ref) {
		for (CtTypeReference typeref : this.exploredReferences) {
			if (typeref.getSimpleName().equals(ref.getSimpleName()) && !typeref.getQualifiedName().equals(ref.getQualifiedName())) {
				return true;
			}
		}
		return false;
	}*/


	/**
	 * Test if the reference can be imported, i.e. test if the importation could lead to a collision.
	 * @param ref
	 * @return true if the ref should be imported.
	 */
	/*protected boolean isTypeInCollision(CtReference ref, boolean fqnMode) {
		if (targetType != null && targetType.getSimpleName().equals(ref.getSimpleName()) && !targetType.equals(ref)) {
			return true;
		}

		try {
			CtElement parent;
			if (ref instanceof CtTypeReference) {
				parent = ref.getParent();
			} else {
				parent = ref;
			}

			// in that case we are trying to import a type because of a literal we are scanning
			// i.e. a string, an int, etc.
			if (parent instanceof CtLiteral) {
				return false;
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
				if ((parent instanceof CtFieldReference) || (parent instanceof CtExecutableReference) || (parent instanceof CtInvocation)) {
					CtReference parentType;
					if (parent instanceof CtInvocation) {
						parentType = ((CtInvocation) parent).getExecutable();
					} else {
						parentType = (CtReference) parent;
					}
					LinkedList<String> qualifiedNameTokens = new LinkedList<>();

					// we don't want to test the current ref name, as we risk to create field import and make autoreference
					if (parentType != parent) {
						qualifiedNameTokens.add(parentType.getSimpleName());
					}

					CtTypeReference typeReference;
					if (parent instanceof CtFieldReference) {
						typeReference = ((CtFieldReference) parent).getDeclaringType();
					} else if (parent instanceof CtExecutableReference) {
						typeReference = ((CtExecutableReference) parent).getDeclaringType();
					} else {
						typeReference = ((CtInvocation) parent).getExecutable().getDeclaringType();
					}

					if (typeReference != null) {
						qualifiedNameTokens.addFirst(typeReference.getSimpleName());

						if (typeReference.getPackage() != null) {
							StringTokenizer token = new StringTokenizer(typeReference.getPackage().getSimpleName(), CtPackage.PACKAGE_SEPARATOR);
							int index = 0;
							while (token.hasMoreElements()) {
								qualifiedNameTokens.add(index, token.nextToken());
								index++;
							}
						}
					}
					if (!qualifiedNameTokens.isEmpty()) {
						// qualified name token are ordered in the reverse order
						// if the first package name is a variable name somewhere, it could lead to a collision
						if (fieldAndMethodsNames.contains(qualifiedNameTokens.getFirst()) || localVariablesOfBlock.contains(qualifiedNameTokens.getFirst())) {
							qualifiedNameTokens.removeFirst();

							if (fqnMode) {
								// in case we are testing a type: we should not import it if its entire name is in collision
								// for example: spoon.Launcher if a field spoon and another one Launcher exists
								if (ref instanceof CtTypeReference) {
									if (qualifiedNameTokens.isEmpty()) {
										return true;
									}
									// but if the other package names are not a variable name, it's ok to import
									for (int i =  0; i < qualifiedNameTokens.size(); i++) {
										String testedToken = qualifiedNameTokens.get(i);
										if (!fieldAndMethodsNames.contains(testedToken) && !localVariablesOfBlock.contains(testedToken)) {
											return true;
										}
									}
									return false;

								// However if it is a static method/field, we always accept to import them in this case
								// It is the last possibility for managing import for us
								} else {
									return true;
								}
							} else {
								// but if the other package names are not a variable name, it's ok to import
								for (int i =  0; i < qualifiedNameTokens.size(); i++) {
									String testedToken = qualifiedNameTokens.get(i);
									if (!fieldAndMethodsNames.contains(testedToken) && !localVariablesOfBlock.contains(testedToken)) {
										return false;
									}
								}
								return true;
							}
						}
					}


				}
				parent = parent.getParent();
			}
		} catch (ParentNotInitializedException e) {
			return false;
		}

		return false;
	}*/
}
