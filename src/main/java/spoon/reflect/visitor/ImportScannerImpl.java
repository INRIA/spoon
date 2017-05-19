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
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.SpoonClassNotFoundException;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * A scanner that calculates the imports for a given model.
 */
public class ImportScannerImpl extends CtScanner implements ImportScanner {

	private static final Collection<String> namesPresentInJavaLang8 =
			Collections.singletonList("FunctionalInterface");
	private static final Collection<String> namesPresentInJavaLang9 = Arrays.asList(
			"ProcessHandle", "StackWalker", "StackFramePermission");

	protected Map<String, CtTypeReference<?>> classImports = new TreeMap<>();
	protected Map<String, CtFieldReference<?>> fieldImports = new TreeMap<>();
	protected Map<String, CtExecutableReference<?>> methodImports = new TreeMap<>();
	//top declaring type of that import
	protected CtTypeReference<?> targetType;
	private Map<String, Boolean> namesPresentInJavaLang = new HashMap<>();
	private Set<String> fieldAndMethodsNames = new HashSet<String>();
	private Set<CtTypeReference> exploredReferences = new HashSet<>(); // list of explored references

	@Override
	public <T> void visitCtFieldRead(CtFieldRead<T> fieldRead) {
		enter(fieldRead);
		scan(fieldRead.getVariable());
		scan(fieldRead.getAnnotations());
		scan(fieldRead.getTypeCasts());
		scan(fieldRead.getVariable());
		scan(fieldRead.getTarget());
		exit(fieldRead);
	}

	@Override
	public <T> void visitCtFieldWrite(CtFieldWrite<T> fieldWrite) {
		enter(fieldWrite);
		scan(fieldWrite.getVariable());
		scan(fieldWrite.getAnnotations());
		scan(fieldWrite.getTypeCasts());
		scan(fieldWrite.getVariable());
		scan(fieldWrite.getTarget());
		exit(fieldWrite);
	}

	@Override
	public <T> void visitCtFieldReference(CtFieldReference<T> reference) {
		enter(reference);
		if (reference.isStatic()) {
			if (!addFieldImport(reference)) {
				scan(reference.getDeclaringType());
			}
		} else {
			scan(reference.getDeclaringType());
		}
		exit(reference);
	}

	@Override
	public <T> void visitCtExecutableReference(
			CtExecutableReference<T> reference) {
		enter(reference);
		if (reference.isStatic()) {
			addMethodImport(reference);
		} else if (reference.isConstructor()) {
			scan(reference.getDeclaringType());
		}
		scan(reference.getActualTypeArguments());
		exit(reference);
	}

	@Override
	public <T> void visitCtInvocation(CtInvocation<T> invocation) {
		enter(invocation);
		scan(invocation.getAnnotations());
		scan(invocation.getTypeCasts());
		scan(invocation.getTarget());
		scan(invocation.getExecutable());
		scan(invocation.getArguments());
		exit(invocation);
	}

	@Override
	public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
		if (!(reference instanceof CtArrayTypeReference)) {
			if (reference.getDeclaringType() == null) {
				addClassImport(reference);
			} else {
				addClassImport(reference.getAccessType());
			}
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
	public <A extends Annotation> void visitCtAnnotationType(
			CtAnnotationType<A> annotationType) {
		addClassImport(annotationType.getReference());
		super.visitCtAnnotationType(annotationType);
	}

	@Override
	public <T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum) {
		addClassImport(ctEnum.getReference());
		super.visitCtEnum(ctEnum);
	}

	@Override
	public <T> void visitCtInterface(CtInterface<T> intrface) {
		addClassImport(intrface.getReference());
		for (CtTypeMember t : intrface.getTypeMembers()) {
			if (!(t instanceof CtType)) {
				continue;
			}
			addClassImport(((CtType) t).getReference());
		}
		super.visitCtInterface(intrface);
	}

	@Override
	public <T> void visitCtClass(CtClass<T> ctClass) {
		addClassImport(ctClass.getReference());
		for (CtTypeMember t : ctClass.getTypeMembers()) {
			if (!(t instanceof CtType)) {
				continue;
			}
			addClassImport(((CtType) t).getReference());
		}
		super.visitCtClass(ctClass);
	}

	@Override
	public <T> void visitCtCatchVariable(CtCatchVariable<T> catchVariable) {
		for (CtTypeReference<?> type : catchVariable.getMultiTypes()) {
			addClassImport(type);
		}
		super.visitCtCatchVariable(catchVariable);
	}

	@Override
	public Collection<CtReference> computeAllImports(CtType<?> simpleType) {
		classImports.clear();
		fieldImports.clear();
		methodImports.clear();
		//look for top declaring type of that simpleType
		targetType = simpleType.getReference().getTopLevelType();
		addClassImport(simpleType.getReference());
		scan(simpleType);

		Collection<CtReference> listallImports = new ArrayList<>();
		listallImports.addAll(this.classImports.values());
		listallImports.addAll(this.fieldImports.values());
		listallImports.addAll(this.methodImports.values());
		return listallImports;
	}

	@Override
	public Collection<CtTypeReference<?>> computeImports(CtElement element) {
		classImports.clear();
		fieldImports.clear();
		methodImports.clear();
		//look for top declaring type of that simpleType

		if (element instanceof CtType) {
			CtType simpleType = (CtType) element;
			targetType = simpleType.getReference().getTopLevelType();
			addClassImport(simpleType.getReference());
			scan(simpleType);
		} else {
			CtType<?> type = element.getParent(CtType.class);
			targetType = type == null ? null : type.getReference().getTopLevelType();
			scan(element);
		}
		return this.classImports.values();
	}

	@Override
	public boolean isImported(CtReference ref) {
		if (ref instanceof CtFieldReference) {
			return isImportedInFieldImports((CtFieldReference) ref);
		} else if (ref instanceof CtExecutableReference) {
			return isImportedInMethodImports((CtExecutableReference) ref);
		} else if (ref instanceof CtTypeReference) {
			return isImportedInClassImports((CtTypeReference) ref);
		} else {
			return false;
		}
	}

	private boolean isThereAnotherClassWithSameNameInAnotherPackage(CtTypeReference<?> ref) {
		for (CtTypeReference typeref : this.exploredReferences) {
			if (typeref.getSimpleName().equals(ref.getSimpleName()) && !typeref.getQualifiedName().equals(ref.getQualifiedName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds a type to the classImports.
	 */
	protected boolean addClassImport(CtTypeReference<?> ref) {
		this.exploredReferences.add(ref);
		if (ref == null) {
			return false;
		}

		if (targetType != null && targetType.getSimpleName().equals(ref.getSimpleName()) && !targetType.equals(ref)) {
			return false;
		}
		if (classImports.containsKey(ref.getSimpleName())) {
			return isImportedInClassImports(ref);
		}
		// don't import unnamed package elements
		if (ref.getPackage() == null || ref.getPackage().isUnnamedPackage()) {
			return false;
		}
		if (ref.getPackage().getSimpleName().equals("java.lang")) {
			if (classNamePresentInJavaLang(ref)) {
				// Don't import class with names clashing with some classes present in java.lang,
				// because it leads to undecidability and compilation errors. I. e. always leave
				// com.mycompany.String fully-qualified.
				return false;
			}
		}
		if (targetType != null && targetType.canAccess(ref) == false) {
			//ref type is not visible in targetType we must not add import for it, java compiler would fail on that.
			return false;
		}

		if (this.isThereAnotherClassWithSameNameInAnotherPackage(ref)) {
			return false;
		}

		// we want to be sure that we are not importing a class because a static field or method we already imported
		// moreover we make exception for same package classes to avoid problems in FQN mode

		if (targetType != null) {
			try {
				CtElement parent = ref.getParent();
				if (parent != null) {
					parent = parent.getParent();
					if (parent != null) {
						if ((parent instanceof CtFieldAccess) || (parent instanceof CtExecutable) || (parent instanceof CtInvocation)) {

							CtTypeReference declaringType;
							CtReference reference;
							CtPackageReference pack = targetType.getPackage();
							if (parent instanceof CtFieldAccess) {
								CtFieldAccess field = (CtFieldAccess) parent;
								CtFieldReference localReference = field.getVariable();
								declaringType = localReference.getDeclaringType();
								reference = localReference;
							} else if (parent instanceof CtExecutable) {
								CtExecutable exec = (CtExecutable) parent;
								CtExecutableReference localReference = exec.getReference();
								declaringType = localReference.getDeclaringType();
								reference = localReference;
							} else if (parent instanceof CtInvocation) {
								CtInvocation invo = (CtInvocation) parent;
								CtExecutableReference localReference = invo.getExecutable();
								declaringType = localReference.getDeclaringType();
								reference = localReference;
							} else {
								declaringType = null;
								reference = null;
							}

							if (reference != null && isImported(reference)) {
								// if we are in the **same** package we do the import for test with method isImported
								if (declaringType != null) {
									if (declaringType.getPackage() != null && !declaringType.getPackage().isUnnamedPackage()) {
										// ignore java.lang package
										if (!declaringType.getPackage().getSimpleName().equals("java.lang")) {
											// ignore type in same package
											if (declaringType.getPackage().getSimpleName()
													.equals(pack.getSimpleName())) {
												classImports.put(ref.getSimpleName(), ref);
												return true;
											}
										}
									}
								}
							}
						}
					}
				}
			} catch (ParentNotInitializedException e) {
			}
			CtPackageReference pack = targetType.getPackage();
			if (ref.getPackage() != null && !ref.getPackage().isUnnamedPackage()) {
				// ignore java.lang package
				if (!ref.getPackage().getSimpleName().equals("java.lang")) {
					// ignore type in same package
					if (ref.getPackage().getSimpleName()
							.equals(pack.getSimpleName())) {
						return false;
					}
				}
			}
		}

		classImports.put(ref.getSimpleName(), ref);
		return true;
	}

	protected boolean isImportedInClassImports(CtTypeReference<?> ref) {
		if (targetType != null) {
			CtPackageReference pack = targetType.getPackage();

			// we consider that if a class belongs to java.lang or the same package than the actual class
			// then it is imported by default
			if (ref.getPackage() != null && !ref.getPackage().isUnnamedPackage()) {
				// ignore java.lang package
				if (!ref.getPackage().getSimpleName().equals("java.lang")) {
					// ignore type in same package
					if (ref.getPackage().getSimpleName()
							.equals(pack.getSimpleName())) {
						return true;
					}
				}
			}
		}

		if (ref.equals(targetType)) {
			return true;
		}

		if (!(ref.isImplicit()) && classImports.containsKey(ref.getSimpleName())) {
			CtTypeReference<?> exist = classImports.get(ref.getSimpleName());
			if (exist.getQualifiedName().equals(ref.getQualifiedName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method is used to check if the declaring type has been already imported, or if it is local
	 * In both case we do not want to import it, even in FQN mode.
	 * @param declaringType
	 * @return true if it is local or imported
	 */
	private boolean declaringTypeIsLocalOrImported(CtTypeReference declaringType) {
		if (declaringType != null) {
			if (isImportedInClassImports(declaringType) || classNamePresentInJavaLang(declaringType)) {
				return true;
			}

			if (!isTypeInCollision(declaringType, false) && addClassImport(declaringType)) {
				return true;
			}

			while (declaringType != null) {
				if (declaringType.equals(targetType)) {
					return true;
				}
				declaringType = declaringType.getDeclaringType();
			}

		}
		return false;
	}

	protected boolean addMethodImport(CtExecutableReference ref) {
		if (this.methodImports.containsKey(ref.getSimpleName())) {
			return isImportedInMethodImports(ref);
		}

		// if the whole class is imported: no need to import the method.
		if (declaringTypeIsLocalOrImported(ref.getDeclaringType())) {
			return false;
		}

		methodImports.put(ref.getSimpleName(), ref);

		// if we are in the same package than target type, we also import class to avoid FQN in FQN mode.
		if (ref.getDeclaringType() != null) {
			if (ref.getDeclaringType().getPackage() != null) {
				if (ref.getDeclaringType().getPackage().equals(this.targetType.getPackage())) {
					addClassImport(ref.getDeclaringType());
				}
			}
		}
		return true;
	}

	protected boolean isImportedInMethodImports(CtExecutableReference<?> ref) {
		if (!(ref.isImplicit()) && methodImports.containsKey(ref.getSimpleName())) {
			CtExecutableReference<?> exist = methodImports.get(ref.getSimpleName());
			if (exist.getSignature().equals(ref.getSignature())) {
				return true;
			}
		}
		return false;
	}

	protected boolean addFieldImport(CtFieldReference ref) {
		if (this.fieldImports.containsKey(ref.getSimpleName())) {
			return isImportedInFieldImports(ref);
		}

		if (declaringTypeIsLocalOrImported(ref.getDeclaringType())) {
			return false;
		}

		fieldImports.put(ref.getSimpleName(), ref);
		return true;
	}

	protected boolean isImportedInFieldImports(CtFieldReference<?> ref) {
		if (!(ref.isImplicit()) && fieldImports.containsKey(ref.getSimpleName())) {
			CtFieldReference<?> exist = fieldImports.get(ref.getSimpleName());
			try {
				if (exist.getFieldDeclaration() != null && exist.getFieldDeclaration().equals(ref.getFieldDeclaration())) {
					return true;
				}
			// in some rare cases we could not access to the field, then we do not import it.
			} catch (SpoonClassNotFoundException notfound) {
				return false;
			}

		}

		return false;
	}

	protected boolean classNamePresentInJavaLang(CtTypeReference<?> ref) {
		Boolean presentInJavaLang = namesPresentInJavaLang.get(ref.getSimpleName());
		if (presentInJavaLang == null) {
			// The following procedure of determining if the handle is present in Java Lang or
			// not produces "false positives" if the analyzed source complianceLevel is > 6.
			// For example, it reports that FunctionalInterface is present in java.lang even
			// for compliance levels 6, 7. But this is not considered a bad thing, in opposite,
			// it makes generated code a little more compatible with future versions of Java.
			if (namesPresentInJavaLang8.contains(ref.getSimpleName())
					|| namesPresentInJavaLang9.contains(ref.getSimpleName())) {
				presentInJavaLang = true;
			} else {
				// Assuming Spoon's own runtime environment is Java 7+
				try {
					Class.forName("java.lang." + ref.getSimpleName());
					presentInJavaLang = true;
				} catch (ClassNotFoundException e) {
					presentInJavaLang = false;
				}
			}
			namesPresentInJavaLang.put(ref.getSimpleName(), presentInJavaLang);
		}
		return presentInJavaLang;
	}

	protected Set<String> lookForLocalVariables(CtElement parent) {
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
	 * Test if the reference can be imported, i.e. test if the importation could lead to a collision.
	 * In FQN mode, it only tests the first package name: if a collision occurs with this first one, it should be imported.
	 * @param ref
	 * @return true if the ref should be imported.
	 */
	protected boolean isTypeInCollision(CtReference ref, boolean fqnMode) {
		if (targetType.getSimpleName().equals(ref.getSimpleName()) && !targetType.equals(ref)) {
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
						// qualified name token are ordered in the reverse order
						// if the first package name is a variable name somewhere, it could lead to a collision
						if (fieldAndMethodsNames.contains(qualifiedNameTokens.getLast()) || localVariablesOfBlock.contains(qualifiedNameTokens.getLast())) {
							qualifiedNameTokens.removeLast();

							if (fqnMode) {
								return true;
							} else {
								// but if the other package names are not a variable name, it's ok to import
								for (int i = qualifiedNameTokens.size() - 1; i > 0; i--) {
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
	}
}
