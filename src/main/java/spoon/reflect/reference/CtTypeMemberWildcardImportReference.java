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
