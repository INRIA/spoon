/*
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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

package spoon.support.reflect.declaration;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.ReferenceTypeFilter;
import spoon.support.builder.SnippetCompiler;

public abstract class CtSimpleTypeImpl<T> extends CtNamedElementImpl implements
		CtSimpleType<T> {

	public Set<CtTypeReference<?>> getUsedTypes(boolean includeSamePackage) {
		Set<CtTypeReference<?>> typeRefs = new HashSet<CtTypeReference<?>>();
		for (CtTypeReference<?> typeRef : Query.getReferences(this,
				new ReferenceTypeFilter<CtTypeReference<?>>(
						CtTypeReference.class))) {
			if (!(typeRef.isPrimitive()
					|| (typeRef instanceof CtArrayTypeReference)
					|| typeRef.toString()
							.equals(CtTypeReference.NULL_TYPE_NAME) || ((typeRef
					.getPackage() != null) && "java.lang".equals(typeRef
					.getPackage().toString())))
					&& !(!includeSamePackage && typeRef.getPackage().equals(
							this.getPackage().getReference()))) {
				typeRefs.add(typeRef);
			}
		}
		return typeRefs;
	}

	List<CtField<?>> fields = new ArrayList<CtField<?>>();

	Set<CtSimpleType<?>> nestedTypes = new TreeSet<CtSimpleType<?>>();

	public Class<T> getActualClass() {
		return getFactory().Type().createReference(this).getActualClass();
	}

	public List<CtField<?>> getAllFields() {
		return getFields();
	}

	public CtSimpleType<?> getDeclaringType() {
		return getParent(CtSimpleType.class);
	}

	public CtField<?> getField(String name) {
		for (CtField<?> f : fields) {
			if (f.getSimpleName().equals(name)) {
				return f;
			}
		}
		return null;
	}

	public List<CtField<?>> getFields() {
		return fields;
	}

	public CtSimpleType<?> getNestedType(final String name) {
		class NestedTypeScanner extends CtScanner {
			CtSimpleType<?> type;

			public void checkType(CtSimpleType<?> type) {
				if (type.getSimpleName().equals(name)
						&& CtSimpleTypeImpl.this
								.equals(type.getDeclaringType())) {
					this.type = type;
				}
			}

			public <U> void visitCtClass(
					spoon.reflect.declaration.CtClass<U> ctClass) {
				scan(ctClass.getNestedTypes());
				scan(ctClass.getConstructors());
				scan(ctClass.getMethods());

				checkType(ctClass);
			}

			public <U> void visitCtInterface(
					spoon.reflect.declaration.CtInterface<U> intrface) {
				scan(intrface.getNestedTypes());
				scan(intrface.getMethods());

				checkType(intrface);
			}

			public <U extends java.lang.Enum<?>> void visitCtEnum(
					spoon.reflect.declaration.CtEnum<U> ctEnum) {
				scan(ctEnum.getNestedTypes());
				scan(ctEnum.getConstructors());
				scan(ctEnum.getMethods());

				checkType(ctEnum);
			}

			public <A extends Annotation> void visitCtAnnotationType(
					CtAnnotationType<A> annotationType) {
				scan(annotationType.getNestedTypes()); 

				checkType(annotationType);
			};

			CtSimpleType<?> getType() {
				return type;
			}
		}
		NestedTypeScanner scanner = new NestedTypeScanner();
		scanner.scan(this);
		return scanner.getType();
	}

	public Set<CtSimpleType<?>> getNestedTypes() {
		return nestedTypes;
	}

	public CtPackage getPackage() {
		if (parent instanceof CtPackage) {
			return (CtPackage) parent;
		} else if (parent instanceof CtSimpleType) {
			return ((CtSimpleType<?>) parent).getPackage();
		} else {
			return null;
		}
	}

	public String getQualifiedName() {
		if ((getPackage() != null)
				&& !getPackage().getSimpleName().equals(
						CtPackage.TOP_LEVEL_PACKAGE_NAME)) {
			return getPackage().getQualifiedName() + "." + getSimpleName();
		}
		return getSimpleName();
	}

	@Override
	public CtTypeReference<T> getReference() {
		return getFactory().Type().createReference(this);
	}

	public boolean isTopLevel() {
		return (getDeclaringType() == null) && (getPackage() != null);
	}

	public void setFields(List<CtField<?>> fields) {
		this.fields = fields;
	}

	public void setNestedTypes(Set<CtSimpleType<?>> nestedTypes) {
		this.nestedTypes = nestedTypes;
	}

	public void compileAndReplaceSnippets() {
		SnippetCompiler.compileAndReplaceSnippetsIn(this);

	}

}
