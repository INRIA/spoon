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
package spoon.support.reflect.declaration;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.declaration.CtModuleRequirement;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtModuleReference;
import spoon.reflect.visitor.CtVisitor;

import java.util.HashSet;
import java.util.Set;

public class CtModuleRequirementImpl extends CtElementImpl implements CtModuleRequirement {
	@MetamodelPropertyField(role = CtRole.MODIFIER)
	private Set<RequiresModifier> requiresModifiers = CtElementImpl.emptySet();

	@MetamodelPropertyField(role = CtRole.MODULE_REF)
	private CtModuleReference moduleReference;

	public CtModuleRequirementImpl() {
	}

	@Override
	public Set<RequiresModifier> getRequiresModifiers() {
		return this.requiresModifiers;
	}

	@Override
	public <T extends CtModuleRequirement> T setRequiresModifiers(Set<RequiresModifier> requiresModifiers) {
		getFactory().getEnvironment().getModelChangeListener().onSetDeleteAll(this, CtRole.MODIFIER, this.requiresModifiers, new HashSet<>(requiresModifiers));
		if (requiresModifiers == null || requiresModifiers.isEmpty()) {
			this.requiresModifiers = CtElementImpl.emptySet();
			return (T) this;
		}

		if (this.requiresModifiers == CtElementImpl.<RequiresModifier>emptySet()) {
			this.requiresModifiers = new HashSet<>();
		}
		this.requiresModifiers.clear();
		for (RequiresModifier requiresModifier : requiresModifiers) {
			getFactory().getEnvironment().getModelChangeListener().onSetAdd(this, CtRole.MODIFIER, this.requiresModifiers, requiresModifier);
			this.requiresModifiers.add(requiresModifier);
		}

		return (T) this;
	}

	@Override
	public CtModuleReference getModuleReference() {
		return this.moduleReference;
	}

	@Override
	public <T extends CtModuleRequirement> T setModuleReference(CtModuleReference moduleReference) {
		if (moduleReference != null) {
			moduleReference.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.MODULE_REF, moduleReference, this.moduleReference);
		this.moduleReference = moduleReference;
		return (T) this;
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtModuleRequirement(this);
	}

	@Override
	public CtModuleRequirement clone() {
		return (CtModuleRequirement) super.clone();
	}
}
