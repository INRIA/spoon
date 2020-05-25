/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.reference;

import static spoon.reflect.path.CtRole.BOUNDING_TYPE;
import static spoon.reflect.path.CtRole.IS_UPPER;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.UnsettableProperty;

public class CtWildcardReferenceImpl extends CtTypeParameterReferenceImpl implements CtWildcardReference {

	@MetamodelPropertyField(role = BOUNDING_TYPE)
	private CtTypeReference<?> superType;

	@MetamodelPropertyField(role = IS_UPPER)
	boolean upper = true;

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtWildcardReference(this);
	}

	public CtWildcardReferenceImpl() {
		simplename = "?";
		setBoundingType(null);
	}

	@Override
	public boolean isUpper() {
		return upper;
	}

	@Override
	public <T extends CtWildcardReference> T setUpper(boolean upper) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, IS_UPPER, upper, this.upper);
		this.upper = upper;
		return (T) this;
	}

	@Override
	public <T extends CtWildcardReference> T setBoundingType(CtTypeReference<?> superType) {
		if (superType != null) {
			superType.setParent(this);
		}

		// ugly but else make testSetterInNodes failed
		if (superType == null) { // if null, set bounding type to object
			superType = getFactory().Type().objectType();
			superType.setImplicit(true);
			superType.setParent(this);
		}

		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, BOUNDING_TYPE, superType, this.superType);
		this.superType = superType;
		return (T) this;
	}

	@Override
	public CtTypeReference<?> getBoundingType() {
		return superType;
	}

	@Override
	@UnsettableProperty
	public <T extends CtReference> T setSimpleName(String simplename) {
		return (T) this;
	}

	@Override
	public CtWildcardReference clone() {
		return (CtWildcardReference) super.clone();
	}

	@Override
	public CtType<Object> getTypeDeclaration() {
		return getFactory().Type().get(Object.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Class<Object> getActualClass() {
		if (isUpper()) {
			return (Class<Object>) getBoundingType().getActualClass();
		}
		return Object.class;
	}

	@Override
	protected boolean isWildcard() {
		return true;
	}
}
