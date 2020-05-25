/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import spoon.SpoonException;
import spoon.experimental.CtUnresolvedImport;
import spoon.javadoc.internal.JavadocDescriptionElement;
import spoon.javadoc.internal.JavadocInlineTag;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.code.CtJavaDocTag;
import spoon.reflect.code.CtTargetedExpression;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtImportKind;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeMemberWildcardImportReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.Experimental;
import spoon.support.util.ModelList;
import spoon.support.visitor.ClassTypingContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Updates list of import statements of compilation unit following {@link CtElement#isImplicit()}.
 * Can be configured to add or remove imports using {@link #setCanAddImports(boolean)} and {@link #setCanRemoveImports(boolean)}.
 * This does not force some references to be implicit, and doesn't fix the wrong implicit which causes conflicts: this fixing done by {@link ImportConflictDetector}
 */
@Experimental
public class ImportCleaner extends ImportAnalyzer<ImportCleaner.Context> {

	private Comparator<CtImport> importComparator;
	private boolean canAddImports = true;
	private boolean canRemoveImports = true;

	@Override
	protected ImportCleanerScanner createScanner() {
		return new ImportCleanerScanner();
	}

	@Override
	protected Context getScannerContextInformation() {
		return ((ImportCleanerScanner) scanner).context;
	}

	@Override
	protected void handleTargetedExpression(CtTargetedExpression<?, ?> targetedExpression, Context context) {
		if (context == null) {
			return;
		}
		CtExpression<?> target = targetedExpression.getTarget();
		if (target == null) {
			if (targetedExpression instanceof CtFieldAccess
				&& ((CtFieldAccess) targetedExpression).getVariable().getDeclaringType() != null
				&& ((CtFieldAccess) targetedExpression).getVariable().getDeclaringType().isSimplyQualified()) {
				context.addImport(((CtFieldAccess) targetedExpression).getVariable().getDeclaringType());
			}
			return;
		}

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

			} else if (targetedExpression instanceof CtInvocation<?>) {
				CtInvocation<?> invocation = (CtInvocation<?>) targetedExpression;
				//import static method
				if (invocation.getExecutable().isStatic()) {
					context.addImport(invocation.getExecutable());
				}
			} else if (targetedExpression instanceof CtFieldAccess<?>) {
				//import static field
				CtFieldAccess<?> fieldAccess = (CtFieldAccess<?>) targetedExpression;
				if (fieldAccess.getVariable().isStatic()) {
					context.addImport(fieldAccess.getVariable());
				}
			} else {
				throw new SpoonException("don't know how to handle: " + targetedExpression.toStringDebug());
			}
		}


	}

	@Override
	protected void handleTypeReference(CtTypeReference<?> reference, Context context, CtRole role) {
		if (context == null) {
			return;
		}
		if (!reference.isImplicit() && reference.isSimplyQualified()) {
			/*
			 * the package is implicit. E.g. `Assert.assertTrue`
			 * where package `org.junit` is implicit
			 */
			context.addImport(reference);
		}
	}

	/** a set of imports for a given compilation unit */
	public class Context {
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
			if (typeRef == null) {
				// we would like to add an import, but we don't know to where
				return;
			}
			CtTypeReference<?> topLevelTypeRef = typeRef.getTopLevelType();
			if (typeRefQNames.contains(topLevelTypeRef.getQualifiedName())) {
				//it is reference to a type of this CompilationUnit. Do not add it
				return;
			}
			CtPackageReference packageRef = topLevelTypeRef.getPackage();
			if (packageRef == null) {
				return;
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

		void onCompilationUnitProcessed(CtCompilationUnit compilationUnit) {
			ModelList<CtImport> existingImports = compilationUnit.getImports();
			Set<CtImport> computedImports = new HashSet<>(this.computedImports.values());
			topfor: for (CtImport oldImport : new ArrayList<>(existingImports)) {
				if (!computedImports.remove(oldImport)) {

					// case: import is required in Javadoc
					for (CtType type: compilationUnit.getDeclaredTypes()) {
						for (CtJavaDoc element: type.getElements(new TypeFilter<>(CtJavaDoc.class))) {
							for (CtJavaDocTag tag: element.getTags()) {
								// case @throws
								if (oldImport.getReference() != null && oldImport.getReference().getSimpleName().equals(tag.getParam())) {
									continue topfor;

								}
							}
							for (JavadocDescriptionElement part : ((CtJavaDoc) element).getJavadocElements()) {
								// case {@link Foo}
								if (part instanceof JavadocInlineTag) {
									String content = ((JavadocInlineTag) part).getContent();
									if (oldImport.getReference() != null && oldImport.getReference().getSimpleName().equals(content)) {
										continue topfor;
									}
								}
							}
						}
					}

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
						if (oldImport instanceof CtUnresolvedImport) {
							//never remove unresolved imports
						} else {
							existingImports.remove(oldImport);
						}
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

	/**
	 * A scanner that initializes context for a compilation unit.
	 */
	public class ImportCleanerScanner extends EarlyTerminatingScanner<Void> {
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
				context.onCompilationUnitProcessed((CtCompilationUnit) e);
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
	public ImportCleaner setCanAddImports(boolean canAddImports) {
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
	public ImportCleaner setCanRemoveImports(boolean canRemoveImports) {
		this.canRemoveImports = canRemoveImports;
		return this;
	}

	public Comparator<CtImport> getImportComparator() {
		return importComparator;
	}

	public ImportCleaner setImportComparator(Comparator<CtImport> importComparator) {
		this.importComparator = importComparator;
		return this;
	}
}
