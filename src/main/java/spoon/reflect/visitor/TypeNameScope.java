/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import spoon.experimental.CtUnresolvedImport;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtImportKind;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtTypeMemberWildcardImportReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.AllTypeMembersFunction;

/**
 * Represents a lexical scope of a type, with all accessible fields, nested type names and method names
 */
class TypeNameScope extends NameScopeImpl {
	private Map<String, CtNamedElement> fieldsByName;
	private Map<String, CtNamedElement> typesByName;
	private Map<String, CtNamedElement> methodsByName;

	TypeNameScope(LexicalScope conflictFinder, CtType<?> p_type) {
		super(conflictFinder, p_type);
	}

	@Override
	public <T> T forEachElementByName(String name, Function<? super CtNamedElement, T> consumer) {
		//1st check scope of type members
		//2nd check scope of type name itself
		T r;
		assureCacheInitialized();
		r = forEachByName(fieldsByName, name, consumer);
		if (r != null) {
			return r;
		}
		r = forEachByName(typesByName, name, consumer);
		if (r != null) {
			return r;
		}
		r = forEachByName(methodsByName, name, consumer);
		if (r != null) {
			return r;
		}
		r = super.forEachElementByName(name, consumer);
		if (r != null) {
			return r;
		}
		return null;
	}

	private void putType(CtType<?> t) {
		putIfNotExists(typesByName, t);
	}
	private void assureCacheInitialized() {
		if (fieldsByName == null) {
			//collect names of type members which are visible in this type
			fieldsByName = new HashMap<>();
			typesByName = new HashMap<>();
			methodsByName = new HashMap<>();
			CtType<?> type = (CtType<?>) getScopeElement();
			type.map(new AllTypeMembersFunction().setMode(AllTypeMembersFunction.Mode.SKIP_PRIVATE)).forEach((CtTypeMember typeMember) -> {
				//the local members are visited first. Then members of super types/interfaces
				if (typeMember instanceof CtField) {
					putIfNotExists(fieldsByName, (CtField<?>) typeMember);
				} else if (typeMember instanceof CtType) {
					putType((CtType<?>) typeMember);
				} else if (typeMember instanceof CtMethod) {
					putIfNotExists(methodsByName, (CtMethod<?>) typeMember);
				}
			});
			if (type.isTopLevel()) {
				CtCompilationUnit cu = type.getPosition().getCompilationUnit();
				if (cu != null) {
					//add types and static fields and methods from compilation unit
					addCompilationUnitNames(cu);
				}
			}
		}
	}

	/*
	 * sort wildcard imports as last. The wildcard import of type has lower priority then explicit import of type
	 */
	private static final Comparator<CtImport> importComparator = new Comparator<CtImport>() {
		@Override
		public int compare(CtImport o1, CtImport o2) {
			CtImportKind k1 = o1.getImportKind();
			CtImportKind k2 = o2.getImportKind();
			return getOrderOfImportKind(k1) - getOrderOfImportKind(k2);
		}

		private int getOrderOfImportKind(CtImportKind ik) {
			switch (ik) {
			case ALL_STATIC_MEMBERS:
				return 2;
			case ALL_TYPES:
				return 1;
			default:
				return 0;
			}
		}
	};

	private void addCompilationUnitNames(CtCompilationUnit compilationUnit) {
		CtType<?> type = (CtType<?>) getScopeElement();
		CtTypeReference<?> typeRef = type.getReference();
		//all imported types and static members are visible too
		compilationUnit.getImports().stream().sorted(importComparator).forEach(aImport -> {
			aImport.accept(new CtImportVisitor() {
				@Override
				public <T> void visitTypeImport(CtTypeReference<T> typeReference) {
					putType(typeReference.getTypeDeclaration());
				}
				@Override
				public <T> void visitMethodImport(CtExecutableReference<T> executableReference) {
					putIfNotExists(methodsByName, executableReference.getExecutableDeclaration());
				}
				@Override
				public <T> void visitFieldImport(CtFieldReference<T> fieldReference) {
					putIfNotExists(fieldsByName, fieldReference.getFieldDeclaration());
				}
				@Override
				public void visitAllTypesImport(CtPackageReference packageReference) {
					CtPackage pack = packageReference.getDeclaration();
					if (pack != null) {
						for (CtType<?> type : pack.getTypes()) {
							//add only types which are not yet imported. Explicit import wins over wildcard import
							putType(type);
						}
					}
				}
				@Override
				public <T> void visitAllStaticMembersImport(CtTypeMemberWildcardImportReference typeReference) {
					CtType<?> type = typeReference.getDeclaration();
					type.map(new AllTypeMembersFunction().setMode(AllTypeMembersFunction.Mode.SKIP_PRIVATE)).forEach((CtTypeMember typeMember) -> {
						if (typeMember.isStatic() && typeRef.canAccess(typeMember)) {
							if (typeMember instanceof CtField) {
								putIfNotExists(fieldsByName, typeMember);
							} else if (typeMember instanceof CtMethod) {
								putIfNotExists(methodsByName, typeMember);
							}
						}
					});
				}
				@Override
				public <T> void visitUnresolvedImport(CtUnresolvedImport ctUnresolvedImport) {
					//there is no usable type member under unresolved import
				}
			});
		});
		//names of all types of same package are visible too, but with lower priority then explicitly imported elements
		CtPackage pack = compilationUnit.getDeclaredPackage();
		if (pack != null) {
			for (CtType<?> packageType : pack.getTypes()) {
				if (packageType != getScopeElement() && !typesByName.containsKey(packageType.getSimpleName())) {
					typesByName.put(packageType.getSimpleName(), packageType);
				}
			}
		}
	}

	//assures that type members nearer to local type are used
	private <T extends CtNamedElement> void putIfNotExists(Map<String, T> map, T element) {
		if (element == null) {
			//noclasspath mode. Ignore that.
			return;
		}
		String name = element.getSimpleName();
		if (!map.containsKey(name)) {
			map.put(name, element);
		}
	}
}
