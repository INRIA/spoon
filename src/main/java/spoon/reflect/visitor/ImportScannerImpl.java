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
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
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
import java.util.StringTokenizer;
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
		scan(fieldRead.getAnnotations());
		scan(fieldRead.getTypeCasts());
		scan(fieldRead.getVariable());
		scan(fieldRead.getTarget());
		exit(fieldRead);
	}

	@Override
	public <T> void visitCtFieldReference(CtFieldReference<T> reference) {
		enter(reference);
		scan(reference.getDeclaringType());
		if (reference.isStatic()) {
			addFieldImport(reference);
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
	public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
		if (!(reference instanceof CtArrayTypeReference)) {
			CtTypeReference typeReference;
			if (reference.getDeclaringType() == null) {
				typeReference = reference;
			} else {
				typeReference = reference.getAccessType();
			}

			if (!this.isTypeInCollision(typeReference, false)) {
				this.addClassImport(typeReference);
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
	public Collection<CtReference> getAllImports() {
		Collection<CtReference> listallImports = new ArrayList<>();
		listallImports.addAll(this.classImports.values());
		listallImports.addAll(this.fieldImports.values());
		listallImports.addAll(this.methodImports.values());
		return listallImports;
	}

	@Override
	public void computeImports(CtElement element) {
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
			if (pack != null && ref.getPackage() != null && !ref.getPackage().isUnnamedPackage()) {
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
			if (pack != null &&  ref.getPackage() != null && !ref.getPackage().isUnnamedPackage()) {
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
			if (!isTypeInCollision(declaringType, false) && addClassImport(declaringType)) {
				return true;
			}

			if (isImportedInClassImports(declaringType) || classNamePresentInJavaLang(declaringType)) {
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
	 * @param ref
	 * @return true if the ref should be imported.
	 */
	protected boolean isTypeInCollision(CtReference ref, boolean fqnMode) {
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
	}
}
