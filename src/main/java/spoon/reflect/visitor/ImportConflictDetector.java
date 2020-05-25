/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtTargetedExpression;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.Experimental;


/**
 * Detects conflicts needed to be required be a fully-qualified name.
 *
 * 1) Example: conflict of field name with an variable name and fixes it by making field target explicit.
 * <code><pre>
 * class A {
 *  int xxx;
 *  void m(String xxx) {
 *    this.xxx //the target `this.` must be explicit, otherwise parameter `String xxx` hides it
 *  }
 * }
 *</pre></code>
 *
 * 2) Example: conflict of package name with an variable name and fixes it by making field target implicit.
 * <code><pre>
 * class A {
 *  int com;
 *  void m() {
 *    com.package.Type.doSomething(); //the package `com` is in conflict with field `com`. Must be imported
 *  }
 * }
 *</pre></code>
 * and fixes them by call of {@link CtElement#setImplicit(boolean)} and {@link CtTypeReference#setSimplyQualified(boolean)}
 */
@Experimental
public class ImportConflictDetector extends ImportAnalyzer<LexicalScope> {

	@Override
	protected LexicalScopeScanner createScanner() {
		return new LexicalScopeScanner();
	}

	@Override
	protected LexicalScope getScannerContextInformation() {
		return ((LexicalScopeScanner) scanner).getCurrentLexicalScope();
	}

	@Override
	protected void handleTargetedExpression(CtTargetedExpression<?, ?> targetedExpression, LexicalScope nameScope) {
		CtExpression<?> target = targetedExpression.getTarget();
		if (target == null) {
			return;
		}
		if (targetedExpression instanceof CtFieldAccess<?>) {
			CtFieldAccess<?> fieldAccess = (CtFieldAccess<?>) targetedExpression;
			if (target.isImplicit()) {
				/*
				 * target is implicit, check whether there is no conflict with an local variable, catch variable or parameter
				 * in case of conflict make it explicit, otherwise the field access is shadowed by that variable.
				 * Search for potential variable declaration until we found a class which declares or inherits this field
				 */
				final CtField<?> field = fieldAccess.getVariable().getFieldDeclaration();
				if (field != null) {
					final String fieldName = field.getSimpleName();
					nameScope.forEachElementByName(fieldName, named -> {
						if (named instanceof CtMethod) {
							//the methods with same name are no problem for field access
							return null;
						}
						if (named == field) {
							return true;
						}
						//another variable declaration was found which is hiding the field declaration for this field access. Make the field access explicit
						target.setImplicit(false);
						return false;
					});
				}
			}
			if (!target.isImplicit()) {
				//the target should be visible in sources
				if (target instanceof CtTypeAccess) {
					//the type has to be visible in sources
					CtTypeAccess<?> typeAccess = (CtTypeAccess<?>) target;
					CtTypeReference<?> accessedTypeRef = typeAccess.getAccessedType();
					checkConflictOfTypeReference(nameScope, accessedTypeRef);
				}
			}
		}
	}

	@Override
	protected void handleTypeReference(CtTypeReference<?> ref, LexicalScope nameScope, CtRole role) {
		if (ref.isImplicit()) {
			/*
			 * the reference is implicit. E.g. `assertTrue();`
			 * when the type `org.junit.Assert` is implicit
			 */
			//check if targeted expression is in conflict
			CtTargetedExpression<?, ?> targetedExpr = getParentIfType(getParentIfType(ref, CtTypeAccess.class), CtTargetedExpression.class);
			if (targetedExpr instanceof CtInvocation<?>) {
				CtInvocation<?> invocation = (CtInvocation<?>) targetedExpr;
				CtExecutableReference<?> importedReference = invocation.getExecutable();
				CtExecutable<?> importedElement = importedReference.getExecutableDeclaration();
				if (importedElement == null) {
					//we have no access to executable - probably no class path mode
					//What to do? Keep it as it is? Or make it fully qualified?
					//keep it as it is for now.
					return;
				}
				if (importedElement instanceof CtMethod) {
					//check if statically imported field or method simple name is not in conflict
					nameScope.forEachElementByName(importedReference.getSimpleName(), named -> {
						if (named instanceof CtMethod<?>) {
							//the method call can be in conflict with Method name only
							if (isSameStaticImport(named, importedElement)) {
								//we have found import of the same method
								return true;
							}
							ref.setImplicit(false);
							ref.setSimplyQualified(true);
							return false;
						}
						//no conflict with type or field name
						return null;
					});
				}
			} else if (targetedExpr instanceof CtFieldAccess<?>) {
				CtFieldAccess<?> fieldAccess = (CtFieldAccess<?>) targetedExpr;
				CtFieldReference<?> importedReference = fieldAccess.getVariable();
				CtElement importedElement = importedReference.getFieldDeclaration();
				if (importedElement == null) {
					//we have no access to executable - probably no class path mode
					//What to do? Keep it as it is? Or make it fully qualified?
					//keep it as it is for now.
					return;
				}
				//check if statically imported field or method simple name is not in conflict
				nameScope.forEachElementByName(importedReference.getSimpleName(), named -> {
					if (named instanceof CtMethod<?>) {
						//field access cannot be in conflict with method name
						return null;
					}
					if (named == importedElement) {
						//we have found import of the same field
						return true;
					}
					//else there is a conflict. Make type explicit and package implicit
					ref.setImplicit(false);
					ref.setSimplyQualified(true);
					return false;
				});
			}
			//else do nothing like in case of implicit type of lambda parameter
			//`(e) -> {...}`
		}
		if (!ref.isImplicit() && ref.isSimplyQualified()) {
			/*
			 * the package is implicit. E.g. `Assert.assertTrue`
			 * where package `org.junit` is implicit
			 */
			String refQName = ref.getQualifiedName();
			//check if type simple name is not in conflict
			nameScope.forEachElementByName(ref.getSimpleName(), named -> {
				if (named instanceof CtMethod) {
					//the methods with same name are no problem for field access
					return null;
				}
				if (named instanceof CtType) {
					CtType<?> type = (CtType<?>) named;
					if (refQName.equals(type.getQualifiedName())) {
						//we have found a declaration of type of the ref. We can use implicit
						return true;
					}
				}
				//we have found a variable, field or type of different name
				ref.setImplicit(false);
				ref.setSimplyQualified(false);
				return false;
			});
		} //else it is already fully qualified
		checkConflictOfTypeReference(nameScope, ref);
	}

	/**
	 * if typeRef package name or simple name is in conflict with any name from nameScope then
	 * solve that conflict by package or type implicit
	 */
	private void checkConflictOfTypeReference(LexicalScope nameScope, CtTypeReference<?> typeRef) {
		if (typeRef == null) {
			return;
		}
		if (!typeRef.isSimplyQualified()) {
			//we have to print fully qualified type name
			//is the first part of package name in conflict with something else?
			if (isPackageNameConflict(nameScope, typeRef)) {
				//the package must be imported, then simple name might be used in this scope
				typeRef.setSimplyQualified(true);
				if (isSimpleNameConflict(nameScope, typeRef)) {
					//there is conflict with simple name too
					typeRef.setImplicit(true);
				}
			}
		} else {
			if (!typeRef.isImplicit()) {
				if (isSimpleNameConflict(nameScope, typeRef)) {
					//there is conflict with simple name
					//use qualified name
					typeRef.setSimplyQualified(false);
				}
			}
		}
	}

	private boolean isPackageNameConflict(LexicalScope nameScope, CtTypeReference<?> typeRef) {
		String fistPackageName = getFirstPackageQName(typeRef);
		if (fistPackageName != null) {
			return Boolean.TRUE == nameScope.forEachElementByName(fistPackageName, named -> {
				if (named instanceof CtMethod) {
					//same method name is not a problem
					return null;
				}
				//the package name is in conflict with field name, variable or type name
				return Boolean.TRUE;
			});
		}
		return false;
	}

	private boolean isSimpleNameConflict(LexicalScope nameScope, CtTypeReference<?> typeRef) {
		String typeQName = typeRef.getQualifiedName();
		//the package name is in conflict check whether type name is in conflict
		return Boolean.TRUE == nameScope.forEachElementByName(typeRef.getSimpleName(), named -> {
			if (named instanceof CtMethod) {
				//same method name is not a problem
				return null;
			}
			if (named instanceof CtType) {
				CtType<?> type = (CtType<?>) named;
				if (typeQName.equals(type.getQualifiedName())) {
					//we found the referenced type declaration -> ok, we can use simple name in this scope
					return Boolean.FALSE;
				}
				//we found another type with same simple name -> conflict
			}
			//there is conflict with simple name
			return Boolean.TRUE;
		});
	}

	private String getFirstPackageQName(CtTypeReference<?> typeRef) {
		if (typeRef != null) {
			CtPackageReference packRef = typeRef.getPackage();
			if (packRef != null) {
				String qname = packRef.getQualifiedName();
				if (qname != null && qname.length() > 0) {
					int idx = qname.indexOf('.');
					if (idx < 0) {
						idx = qname.length();
					}
					return qname.substring(0, idx);
				}
			}
		}
		return null;
	}
	/**
	 * @return true if two methods can come from same static import
	 *
	 * For example `import static org.junit.Assert.assertEquals;`
	 * imports methods with signatures:
	 * assertEquals(Object, Object)
	 * assertEquals(Object[], Object[])
	 * assertEquals(long, long)
	 * ...
	 */
	private static boolean isSameStaticImport(CtNamedElement m1, CtNamedElement m2) {
		if (m1 instanceof CtTypeMember && m2 instanceof CtTypeMember) {
			if (m1.getSimpleName().equals(m2.getSimpleName())) {
				CtType<?> declType1 = ((CtTypeMember) m1).getDeclaringType();
				CtType<?> declType2 = ((CtTypeMember) m2).getDeclaringType();
				//may be we should check isSubTypeOf instead
				return declType1 == declType2;
			}
		}
		return false;
	}
}
