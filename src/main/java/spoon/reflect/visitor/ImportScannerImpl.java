package spoon.reflect.visitor;

import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtTargetedAccess;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.*;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * A scanner that calculates the imports for a given model.
 */
public class ImportScannerImpl extends CtScanner implements ImportScanner {
	private Map<String, CtTypeReference<?>> imports = new TreeMap<String, CtTypeReference<?>>();

	/**
	 * Calculates needed imports for the given field access.
	 */
	@Override
	public <T> void visitCtTargetedAccess(CtTargetedAccess<T> targetedAccess) {
		enter(targetedAccess);
		scan(targetedAccess.getVariable());
		// scan(fieldAccess.getType());
		scan(targetedAccess.getAnnotations());
		scanReferences(targetedAccess.getTypeCasts());
		scan(targetedAccess.getVariable());
		scan(targetedAccess.getTarget());
		exit(targetedAccess);
	}

	@Override
	public <T> void visitCtFieldReference(CtFieldReference<T> reference) {
		enterReference(reference);
		scan(reference.getDeclaringType());
		// scan(reference.getType());
		exitReference(reference);
	}

	@Override
	public <T> void visitCtExecutableReference(
			CtExecutableReference<T> reference) {
		enterReference(reference);
		if (reference.getDeclaringType() != null
				&& reference.getDeclaringType().getDeclaringType() == null) {
			addImport(reference.getDeclaringType());
		}
		scanReferences(reference.getActualTypeArguments());
		exitReference(reference);
	}

	@Override
	public <T> void visitCtInvocation(CtInvocation<T> invocation) {
		// For a ctinvocation, we don't have to import declaring type
		scan(invocation.getTarget());
	}

	@Override
	public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
		if (!(reference instanceof CtArrayTypeReference)) {
			if (reference.getDeclaringType() == null) {
				addImport(reference);
			} else {
				addImport(reference.getDeclaringType());
			}
		}
		super.visitCtTypeReference(reference);

	}

	@Override
	public <A extends Annotation> void visitCtAnnotationType(
			CtAnnotationType<A> annotationType) {
		addImport(annotationType.getReference());
		super.visitCtAnnotationType(annotationType);
	}

	@Override
	public <T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum) {
		addImport(ctEnum.getReference());
		super.visitCtEnum(ctEnum);
	}

	@Override
	public <T> void visitCtInterface(CtInterface<T> intrface) {
		addImport(intrface.getReference());
		for (CtSimpleType<?> t : intrface.getNestedTypes()) {
			addImport(t.getReference());
		}
		super.visitCtInterface(intrface);
	}

	@Override
	public <T> void visitCtClass(CtClass<T> ctClass) {
		addImport(ctClass.getReference());
		for (CtSimpleType<?> t : ctClass.getNestedTypes()) {
			addImport(t.getReference());
		}
		super.visitCtClass(ctClass);
	}

	@Override
	public Collection<CtTypeReference<?>> computeImports(
			CtSimpleType<?> simpleType) {
		addImport(simpleType.getReference());
		scan(simpleType);
		return getImports(simpleType);
	}

	@Override
	public boolean isImported(CtTypeReference<?> ref) {
		if (imports.containsKey(ref.getSimpleName())) {
			CtTypeReference<?> exist = imports.get(ref.getSimpleName());
			if (exist.getQualifiedName().equals(ref.getQualifiedName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets imports in imports Map for the key simpleType given.
	 *
	 * @param simpleType
	 * @return Collection of {@link spoon.reflect.reference.CtTypeReference}
	 */
	private Collection<CtTypeReference<?>> getImports(
			CtSimpleType<?> simpleType) {
		if (imports.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		CtPackageReference pack = ((CtTypeReference<?>) imports
				.get(simpleType.getSimpleName())).getPackage();
		Collection<CtTypeReference<?>> refs = new ArrayList<CtTypeReference<?>>();
		for (CtTypeReference ref : imports.values()) {
			// ignore non-top-level type
			if (ref.getPackage() != null) {
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
		return Collections.unmodifiableCollection(refs);
	}

	/**
	 * Adds a type to the imports.
	 */
	private boolean addImport(CtTypeReference<?> ref) {
		if (imports.containsKey(ref.getSimpleName())) {
			return isImported(ref);
		}
		imports.put(ref.getSimpleName(), ref);
		return true;
	}
}