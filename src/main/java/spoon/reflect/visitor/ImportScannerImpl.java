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
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A scanner that calculates the classImports for a given model.
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
	public Collection<CtReference> computeImports(CtType<?> simpleType) {
		classImports.clear();
		//look for top declaring type of that simpleType
		targetType = simpleType.getReference().getTopLevelType();
		addClassImport(simpleType.getReference());
		scan(simpleType);

		Collection<CtReference> listallImports = new ArrayList<>();
		listallImports.addAll(getClassImports());
		listallImports.addAll(getFieldImports());
		listallImports.addAll(getMethodImports());
		return listallImports;
	}

	@Override
	public void computeImports(CtElement element) {
		classImports.clear();
		//look for top declaring type of that element
		CtType<?> type = element.getParent(CtType.class);
		targetType = type == null ? null : type.getReference().getTopLevelType();
		scan(element);
	}

	@Override
	public boolean isImported(CtReference ref) {
		if (ref instanceof CtFieldReference) {
			return isImportedInFieldImports((CtFieldReference)ref);
		} else if (ref instanceof CtExecutableReference) {
			return isImportedInMethodImports((CtExecutableReference)ref);
		} else if (ref instanceof CtTypeReference) {
			return isImportedInClassImports((CtTypeReference)ref);
		} else {
			return false;
		}
	}

	/**
	 * Gets classImports in classImports Map for the key simpleType given.
	 *
	 * @return Collection of {@link spoon.reflect.reference.CtTypeReference}
	 */
	protected Collection<CtTypeReference<?>> getClassImports() {
		if (classImports.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		CtPackageReference pack = targetType.getPackage();
		List<CtTypeReference<?>> refs = new ArrayList<>();
		for (CtTypeReference<?> ref : classImports.values()) {
			// ignore non-top-level type
			if (ref.getPackage() != null && !ref.getPackage().isUnnamedPackage()) {
				// ignore java.lang package
				if (!ref.getPackage().getSimpleName().equals("java.lang")) {
					// ignore type in same package
					if (!ref.getPackage().getSimpleName()
							.equals(pack.getSimpleName())) {
						refs.add(ref);
					}
				}
			}
		}
		return Collections.unmodifiableList(refs);
	}

	/**
	 * Gets methodImports
	 *
	 * @return Collection of {@link spoon.reflect.reference.CtExecutableReference}
	 */
	protected Collection<CtExecutableReference<?>> getMethodImports() {
		if (methodImports.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		CtPackageReference pack = targetType.getPackage();
		List<CtExecutableReference<?>> refs = new ArrayList<>();
		for (CtExecutableReference<?> ref : methodImports.values()) {
			// ignore non-top-level type
			if (ref.getDeclaringType() != null) {
				if (ref.getDeclaringType().getPackage() != null && !ref.getDeclaringType().getPackage().isUnnamedPackage()) {
					// ignore java.lang package
					if (!ref.getDeclaringType().getPackage().getSimpleName().equals("java.lang")) {
						// ignore type in same package
						if (!ref.getDeclaringType().getPackage().getSimpleName()
								.equals(pack.getSimpleName())) {
							refs.add(ref);
						}
					}
				}
			}
		}
		return Collections.unmodifiableList(refs);
	}

	/**
	 * Gets fieldImports
	 *
	 * @return Collection of {@link spoon.reflect.reference.CtFieldReference}
	 */
	protected Collection<CtFieldReference<?>> getFieldImports() {
		if (fieldImports.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		CtPackageReference pack = targetType.getPackage();
		List<CtFieldReference<?>> refs = new ArrayList<>();
		for (CtFieldReference<?> ref : fieldImports.values()) {
			// ignore non-top-level type
			if (ref.getDeclaringType().getPackage() != null && !ref.getDeclaringType().getPackage().isUnnamedPackage()) {
				// ignore java.lang package
				if (!ref.getDeclaringType().getPackage().getSimpleName().equals("java.lang")) {
					// ignore type in same package
					if (!ref.getDeclaringType().getPackage().getSimpleName()
							.equals(pack.getSimpleName())) {
						refs.add(ref);
					}
				}
			}
		}
		return Collections.unmodifiableList(refs);
	}

	/**
	 * Adds a type to the classImports.
	 */
	protected boolean addClassImport(CtTypeReference<?> ref) {
		if (classImports.containsKey(ref.getSimpleName())) {
			return isImportedInClassImports(ref);
		}
		// don't import unnamed package elements
		if (ref.getPackage() == null || ref.getPackage().isUnnamedPackage()) {
			return false;
		}
		if (!ref.getPackage().getSimpleName().equals("java.lang")) {
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

		// we want to be sure that we are not importing a class because a static field or method we already imported
		// moreover we make exception for same package classes to avoid problems in FQN mode
		try {
			CtElement parent = ref.getParent();
			if (parent != null) {
				parent = parent.getParent();
				if (parent != null) {
					if ((parent instanceof CtFieldAccess) || (parent instanceof CtExecutable)) {
						CtReference reference;

						if (parent instanceof CtFieldAccess) {
							CtFieldAccess field = (CtFieldAccess)parent;
							reference = field.getVariable();
						} else {
							CtExecutable exec = (CtExecutable)parent;
							reference = exec.getReference();
						}

						if (isImported(reference)) {
							if (ref.getDeclaringType() != null) {
								if (!ref.getDeclaringType().getPackage().equals(this.targetType.getPackage())) {
									return false;
								}
							} else {
								return false;
							}
						}
					}
				}
			}
		} catch (ParentNotInitializedException e) {
		}


		//note: we must add the type refs from the same package too, to assure that isImported(typeRef) returns true for them
		//these type refs are removed in #getClassImports()
		classImports.put(ref.getSimpleName(), ref);
		return true;
	}

	private boolean isImportedInClassImports(CtTypeReference<?> ref) {
		if (!(ref.isImplicit()) && classImports.containsKey(ref.getSimpleName())) {
			CtTypeReference<?> exist = classImports.get(ref.getSimpleName());
			if (exist.getQualifiedName().equals(ref.getQualifiedName())) {
				return true;
			}
		}
		return false;
	}

	protected boolean addMethodImport(CtExecutableReference ref) {
		if (this.methodImports.containsKey(ref.getSimpleName())) {
			return isImportedInMethodImports(ref);
		}

		// if the whole class is imported: no need to import the method.
		if (ref.getDeclaringType() != null && isImportedInClassImports(ref.getDeclaringType())) {
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

	private boolean isImportedInMethodImports(CtExecutableReference<?> ref) {
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

		fieldImports.put(ref.getSimpleName(), ref);
		return true;
	}

	private boolean isImportedInFieldImports(CtFieldReference<?> ref) {
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
		CtTypeReference typeRef = ref.getDeclaringType();

		if (typeRef != null) {
			return isImportedInClassImports(typeRef);
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
}
