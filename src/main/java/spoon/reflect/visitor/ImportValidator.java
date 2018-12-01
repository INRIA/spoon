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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import spoon.SpoonException;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtTargetedExpression;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtImportKind;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeMemberWildcardImportReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.Experimental;
import spoon.support.util.ModelList;
import spoon.support.visitor.ClassTypingContext;


/**
 * Updates list of import statements of compilation unit following {@link CtElement#isImplicit()}.
 * It doesn't call {@link CtElement#setImplicit(boolean)}.
 * It doesn't fix wrong used implicit which causes conflicts. The fixing is task of {@link NameConflictValidator}
 */
@Experimental
public class ImportValidator extends AbstractCompilationUnitImportsProcessor<ImportValidator.MyScanner, ImportValidator.Context> {

	private Comparator<CtImport> importComparator;
	private boolean canAddImports = true;
	private boolean canRemoveImports = true;

	@Override
	protected MyScanner createRawScanner() {
		return new MyScanner();
	}

	@Override
	protected Context getContext(MyScanner scanner) {
		return scanner.context;
	}

	@Override
	protected void handleTargetedExpression(Context context, CtRole role, CtTargetedExpression<?, ?> targetedExpression, CtExpression<?> target) {
		if (target != null && target.isImplicit()) {
			if (target instanceof CtTypeAccess) {
				if (targetedExpression instanceof CtFieldAccess) {
					context.addImport(((CtFieldAccess<?>) targetedExpression).getVariable());
				} else if (targetedExpression instanceof CtInvocation) {
					CtExecutableReference<?> execRef = ((CtInvocation<?>) targetedExpression).getExecutable();
					if (execRef.isStatic()) {
						context.addImport(execRef);
					}
				}
			} else if (target instanceof CtVariableAccess) {
				throw new SpoonException("TODO");
			}
		}
	}

	@Override
	protected void handleTypeReference(Context context, CtRole role, CtTypeReference<?> reference) {
		if (reference.isImplicit()) {
			/*
			 * the reference is implicit. E.g. `assertTrue();`
			 * where type `org.junit.Assert` is implicit
			 */
			CtTargetedExpression<?, ?> targetedExpr = getParentIfType(getParentIfType(reference, CtTypeAccess.class), CtTargetedExpression.class);
			if (targetedExpr != null) {
				if (targetedExpr instanceof CtInvocation<?>) {
					CtInvocation<?> invocation = (CtInvocation<?>) targetedExpr;
					//import static method
					context.addImport(invocation.getExecutable());
				} else if (targetedExpr instanceof CtFieldAccess<?>) {
					//import static field
					CtFieldAccess<?> fieldAccess = (CtFieldAccess<?>) targetedExpr;
					context.addImport(fieldAccess.getVariable());
				}
			}
			//else do nothing. E.g. in case of implicit type of lambda parameter
			//`(e) -> {...}`
		} else if (reference.isImplicitParent()) {
			/*
			 * the package is implicit. E.g. `Assert.assertTrue`
			 * where package `org.junit` is implicit
			 */
			context.addImport(reference);
		}
	}

	class Context {
		private CtCompilationUnit compilationUnit;
		private Map<String, CtImport> computedImports;
		private String packageQName;
		private Set<String> typeRefQNames;

		Context(CtCompilationUnit cu) {
			this.compilationUnit = cu;
			CtPackage pckg = cu.getDeclaredPackage();
			if (pckg != null) {
				this.packageQName = pckg.getReference().getQualifiedName();
			}
			this.typeRefQNames = cu.getDeclaredTypeReferences().stream().map(CtTypeReference::getQualifiedName).collect(Collectors.toSet());
			computedImports = new HashMap<>();
		}

		Factory getFactory() {
			return compilationUnit.getFactory();
		}

		void addImport(CtReference ref) {
			if (ref == null) {
				return;
			}
			//check that we do not add reference to a local type
			CtTypeReference<?> typeRef;
			if (ref instanceof CtExecutableReference) {
				typeRef = ((CtExecutableReference<?>) ref).getDeclaringType();
			} else if (ref instanceof CtFieldReference<?>) {
				typeRef = ((CtFieldReference<?>) ref).getDeclaringType();
			} else if (ref instanceof CtTypeReference<?>) {
				typeRef = (CtTypeReference<?>) ref;
			} else {
				throw new SpoonException("Unexpected reference type " + ref.getClass());
			}
			CtTypeReference<?> topLevelTypeRef = typeRef.getTopLevelType();
			if (typeRefQNames.contains(topLevelTypeRef.getQualifiedName())) {
				//it is reference to a type of this CompilationUnit. Do not add it
				return;
			}
			CtPackageReference packageRef = topLevelTypeRef.getPackage();
			if (packageRef == null) {
				throw new SpoonException("Type reference has no package");
			}
			if ("java.lang".equals(packageRef.getQualifiedName())) {
				//java.lang is always imported implicitly. Ignore it
				return;
			}
			if (Objects.equals(packageQName, packageRef.getQualifiedName())) {
				//it is reference to a type of the same package. Do not add it
				return;
			}
			String importRefID = getImportRefID(ref);
			if (!computedImports.containsKey(importRefID)) {
				computedImports.put(importRefID, getFactory().Type().createImport(ref));
			}
		}

		void onComilationUnitProcessed(CtCompilationUnit compilationUnit) {
			ModelList<CtImport> existingImports = compilationUnit.getImports();
			Set<CtImport> computedImports = new HashSet<>(this.computedImports.values());
			for (CtImport oldImport : new ArrayList<>(existingImports)) {
				if (!computedImports.remove(oldImport)) {
					if (oldImport.getImportKind() == CtImportKind.ALL_TYPES) {
						if (removeAllTypeImportWithPackage(computedImports, ((CtPackageReference) oldImport.getReference()).getQualifiedName())) {
							//this All types import still imports some type. Keep it
							continue;
						}
					}
					if (oldImport.getImportKind() == CtImportKind.ALL_STATIC_MEMBERS) {
						if (removeAllStaticTypeMembersImportWithType(computedImports, ((CtTypeMemberWildcardImportReference) oldImport.getReference()).getTypeReference())) {
							//this All types import still imports some type. Keep it
							continue;
						}
					}
					//the import doesn't exist in computed imports. Remove it
					if (canRemoveImports) {
						existingImports.remove(oldImport);
					}
				}
			}

			//add new imports
			if (canAddImports) {
				existingImports.addAll(computedImports);
			}
			if (importComparator != null) {
				existingImports.set(existingImports.stream().sorted(importComparator).collect(Collectors.toList()));
			}
		}
	}

	/**
	 * @return fast unique identification of reference. It is not the same like printing of import, because it needs to handle access path.
	 */
	private static String getImportRefID(CtReference ref) {
		if (ref == null) {
			throw new SpoonException("Null import refrence");
		}
		if (ref instanceof CtFieldReference) {
			CtFieldReference fieldRef = (CtFieldReference) ref;
			return fieldRef.getDeclaringType().getQualifiedName() + "." + fieldRef.getSimpleName();
		}
		if (ref instanceof CtExecutableReference) {
			CtExecutableReference execRef = (CtExecutableReference) ref;
			return execRef.getDeclaringType().getQualifiedName() + "." + execRef.getSimpleName();
		}
		if (ref instanceof CtTypeMemberWildcardImportReference) {
			CtTypeMemberWildcardImportReference wildRef = (CtTypeMemberWildcardImportReference) ref;
			return wildRef.getTypeReference().getQualifiedName() + ".*";
		}
		if (ref instanceof CtTypeReference) {
			CtTypeReference typeRef = (CtTypeReference) ref;
			return typeRef.getQualifiedName();
		}
		throw new SpoonException("Unexpected import type: " + ref.getClass());
	}

	/**
	 * removes all type imports with the same package from the `imports`
	 * @return true if at least one import with the same package exists
	 */
	private boolean removeAllTypeImportWithPackage(Set<CtImport> imports, String packageName) {
		boolean found = false;
		for (Iterator iter = imports.iterator(); iter.hasNext();) {
			CtImport newImport = (CtImport) iter.next();
			if (newImport.getImportKind() == CtImportKind.TYPE) {
				CtTypeReference<?> typeRef = (CtTypeReference<?>) newImport.getReference();
				if (typeRef.getPackage() != null && packageName.equals(typeRef.getPackage().getQualifiedName())) {
					found = true;
					if (canRemoveImports) {
						iter.remove();
					}
				}
			}
		}
		return found;
	}

	/**
	 * removes all static type member imports with the same type from `imports`
	 * @return true if at least one import with the same type exists
	 */
	private boolean removeAllStaticTypeMembersImportWithType(Set<CtImport> imports, CtTypeReference<?> typeRef) {
		//the cached type hierarchy of typeRef
		ClassTypingContext contextOfTypeRef = new ClassTypingContext(typeRef);
		Iterator<CtImport> iter = imports.iterator();
		class Visitor extends CtAbstractImportVisitor {
			boolean found = false;
			@Override
			public <T> void visitFieldImport(CtFieldReference<T> fieldReference) {
				checkType(fieldReference.getDeclaringType());
			}
			@Override
			public <T> void visitMethodImport(CtExecutableReference<T> executableReference) {
				checkType(executableReference.getDeclaringType());
			}
			void checkType(CtTypeReference<?> importTypeRef) {
				if (contextOfTypeRef.isSubtypeOf(importTypeRef)) {
					found = true;
					if (canRemoveImports) {
						iter.remove();
					}
				}
			}
		}
		Visitor visitor = new Visitor();
		while (iter.hasNext()) {
			iter.next().accept(visitor);
		}
		return visitor.found;
	}

	class MyScanner extends EarlyTerminatingScanner<Void> {
		Context context;
		@Override
		protected void enter(CtElement e) {
			if (e instanceof CtCompilationUnit) {
				context = new Context((CtCompilationUnit) e);
			}
		}

		@Override
		protected void exit(CtElement e) {
			if (e instanceof CtCompilationUnit) {
				context.onComilationUnitProcessed((CtCompilationUnit) e);
			}
		}
	}

	/**
	 * @return true if this processor is allowed to add new imports
	 */
	public boolean isCanAddImports() {
		return canAddImports;
	}

	/**
	 * @param canAddImports true if this processor is allowed to add new imports
	 */
	public ImportValidator setCanAddImports(boolean canAddImports) {
		this.canAddImports = canAddImports;
		return this;
	}

	/**
	 * @return true if this processor is allowed to remove imports
	 */
	public boolean isCanRemoveImports() {
		return canRemoveImports;
	}

	/**
	 * @param canRemoveImports true if this processor is allowed to remove imports
	 */
	public ImportValidator setCanRemoveImports(boolean canRemoveImports) {
		this.canRemoveImports = canRemoveImports;
		return this;
	}

	public Comparator<CtImport> getImportComparator() {
		return importComparator;
	}

	public ImportValidator setImportComparator(Comparator<CtImport> importComparator) {
		this.importComparator = importComparator;
		return this;
	}
}
