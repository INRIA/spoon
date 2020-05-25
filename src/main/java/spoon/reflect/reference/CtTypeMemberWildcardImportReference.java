/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.reference;

import java.lang.annotation.Annotation;
import java.util.List;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.path.CtRole;
import spoon.support.DerivedProperty;
import spoon.support.UnsettableProperty;

/**
 * This interface defines a import reference to all static type members of a type.<br>
 * Example:
 * <code>somePackage.Type.*;</code>
 */
public interface CtTypeMemberWildcardImportReference extends CtReference {

	/**
	 * Returns the fully qualified name of type followed by `.*`
	 */
	@DerivedProperty
	String getSimpleName();

	@Override
	@UnsettableProperty
	<T extends CtReference> T setSimpleName(String simpleName);

	@PropertyGetter(role = CtRole.TYPE_REF)
	CtTypeReference<?> getTypeReference();

	@PropertySetter(role = CtRole.TYPE_REF)
	CtTypeMemberWildcardImportReference setTypeReference(CtTypeReference<?> typeReference);

	@Override
	CtTypeMemberWildcardImportReference clone();

	@Override
	@DerivedProperty
	CtType<?> getDeclaration();

	@Override
	@DerivedProperty
	List<CtAnnotation<? extends Annotation>> getAnnotations();

	@Override
	@UnsettableProperty
	<E extends CtElement> E addAnnotation(CtAnnotation<? extends Annotation> annotation);

	@Override
	@UnsettableProperty
	boolean removeAnnotation(CtAnnotation<? extends Annotation> annotation);

	@Override
	@UnsettableProperty
	<E extends CtElement> E setAnnotations(List<CtAnnotation<? extends Annotation>> annotation);

	@Override
	@DerivedProperty
	boolean isImplicit();

	@Override
	@UnsettableProperty
	<E extends CtElement> E setImplicit(boolean b);
}
