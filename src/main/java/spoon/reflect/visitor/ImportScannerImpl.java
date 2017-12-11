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
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtNamedElement;
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
import java.util.stream.Collectors;

/**
 * This scanner tries to optimize the imports in Spoon.
 */
public class ImportScannerImpl extends CtScanner implements ImportScanner {
	private static final int MINIMAL_JAVA_VERSION_FOR_STATIC_IMPORTS = 5;
	private Factory factory;
	private List<CtImport> imports = CtElementImpl.emptyList();
	private Set<CtImport> removedImports = CtElementImpl.emptySet();
	private Set<CtReference> referenceInCollision = CtElementImpl.emptySet();
	protected Set<String> targetTypeNames;

	//top declaring type of that import
	protected CtTypeReference<?> targetType;

	public ImportScannerImpl(Factory factory) {
		this.factory = factory;
		this.targetTypeNames = new HashSet<>();
	}

	/**
	 * @deprecated Use constructor with factory parameter
	 */
	@Deprecated
	public ImportScannerImpl() {
		this.targetTypeNames = new HashSet<>();
	}

	@Override
	public void enter(CtElement ctElement) {
		if (ctElement instanceof CtNamedElement) {
			this.targetTypeNames.add(((CtNamedElement) ctElement).getSimpleName());
		}
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
		if (reference.isStatic() && this.getFactory().getEnvironment().getComplianceLevel() >= MINIMAL_JAVA_VERSION_FOR_STATIC_IMPORTS) {
			// we want to check it in order to put the value
			// in the list of reference in collision
			// for the check when writing in FQN or not
			this.isTypeInCollision(reference);
			if (reference.getDeclaringType() != null && !isImported(reference.getDeclaringType())) {
				this.addImport(reference);
				return;
			}
		}
		super.visitCtFieldReference(reference);
	}

	@Override
	public <T> void visitCtExecutableReference(CtExecutableReference<T> reference) {
		// if it's a constructor, it's the only circumstance where we can call the super
		// we don't want to call it for another executable as we don't want to import its declaring type
		if (reference.isConstructor()) {
			super.visitCtExecutableReference(reference);
		} else if (reference.isStatic() && this.getFactory().getEnvironment().getComplianceLevel() >= MINIMAL_JAVA_VERSION_FOR_STATIC_IMPORTS) {
			// we want to check it in order to put the value
			// in the list of reference in collision
			// for the check when writing in FQN or not
			this.isTypeInCollision(reference);
			if (reference.getDeclaringType() != null && !isImported(reference.getDeclaringType())) {
				this.addImport(reference);
				return;
			}
		}

		// we must check if there is a type argument to call the right import
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
		// if it's imported, it cannot be in collision
		if (this.isEffectivelyImported(reference)) {
			return false;
		}
		if (this.referenceInCollision.contains(reference)) {
			return true;
		}

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

		String referenceName = reference.getSimpleName();

		for (CtImport ctImport : this.imports) {
			if (ctImport.getReference().getSimpleName().equals(referenceName)) {
				this.addReferenceInCollision(reference);
				return true;
			}
		}

		if (this.targetTypeNames.contains(referenceName)) {
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
			this.setTargetType(simpleType.getReference().getTopLevelType());
			scan(simpleType);
		} else {
			CtType<?> type = element.getParent(CtType.class);
			if (type != null) {
				this.setTargetType(type.getReference().getTopLevelType());
			}
			scan(element);
		}
	}

	protected void setTargetType(CtTypeReference targetType) {
		this.targetType = targetType;
		this.targetTypeNames.addAll(this.targetType.getAllFields().stream().map(CtFieldReference::getSimpleName).collect(Collectors.toList()));
		this.targetTypeNames.addAll(this.targetType.getAllExecutables().stream().map(CtExecutableReference::getSimpleName).collect(Collectors.toList()));
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

}
