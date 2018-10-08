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


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import spoon.refactoring.Refactoring;
import spoon.reflect.ModelElementContainerDefaultCapacities;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.filter.AllTypeMembersFunction;
import spoon.support.reflect.CtExtendedModifier;
import spoon.support.reflect.CtModifierHandler;
import spoon.support.visitor.ClassTypingContext;




/**
 * The implementation for {@link spoon.reflect.declaration.CtMethod}.
 *
 * @author Renaud Pawlak
 */
public class CtMethodImpl<T> extends CtExecutableImpl<T> implements CtMethod<T> {
	private static final long serialVersionUID = 1L;

	@MetamodelPropertyField(role = CtRole.TYPE)
	CtTypeReference<T> returnType;

	@MetamodelPropertyField(role = CtRole.IS_DEFAULT)
	boolean defaultMethod = false;

	@MetamodelPropertyField(role = CtRole.TYPE_PARAMETER)
	List<CtTypeParameter> formalCtTypeParameters = emptyList();

	@MetamodelPropertyField(role = CtRole.MODIFIER)
	private CtModifierHandler modifierHandler = new CtModifierHandler(this);

	public CtMethodImpl() {
	}

	@Override
	public void accept(CtVisitor v) {
		v.visitCtMethod(this);
	}

	@Override
	public CtTypeReference<T> getType() {
		return returnType;
	}

	@Override
	public CtMethodImpl<T> setType(CtTypeReference<T> type) {
		if (type != null) {
			type.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.TYPE, type, this.returnType);
		this.returnType = type;
		return this;
	}

	@Override
	public boolean isDefaultMethod() {
		return defaultMethod;
	}

	@Override
	public CtMethodImpl<T> setDefaultMethod(boolean defaultMethod) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.IS_DEFAULT, defaultMethod, this.defaultMethod);
		this.defaultMethod = defaultMethod;
		return this;
	}

	@Override
	public List<CtTypeParameter> getFormalCtTypeParameters() {
		return formalCtTypeParameters;
	}

	@Override
	public CtMethodImpl<T> setFormalCtTypeParameters(List<CtTypeParameter> formalTypeParameters) {
		getFactory().getEnvironment().getModelChangeListener().onListDeleteAll(this, CtRole.TYPE_PARAMETER, this.formalCtTypeParameters, new ArrayList<>(this.formalCtTypeParameters));
		if (formalTypeParameters == null || formalTypeParameters.isEmpty()) {
			this.formalCtTypeParameters = CtElementImpl.emptyList();
			return this;
		}
		if (this.formalCtTypeParameters == CtElementImpl.<CtTypeParameter>emptyList()) {
			this.formalCtTypeParameters = new ArrayList<>(ModelElementContainerDefaultCapacities.TYPE_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		this.formalCtTypeParameters.clear();
		for (CtTypeParameter formalTypeParameter : formalTypeParameters) {
			addFormalCtTypeParameter(formalTypeParameter);
		}
		return this;
	}

	@Override
	public CtMethodImpl<T> addFormalCtTypeParameter(CtTypeParameter formalTypeParameter) {
		if (formalTypeParameter == null) {
			return this;
		}
		if (formalCtTypeParameters == CtElementImpl.<CtTypeParameter>emptyList()) {
			formalCtTypeParameters = new ArrayList<>(ModelElementContainerDefaultCapacities.TYPE_TYPE_PARAMETERS_CONTAINER_DEFAULT_CAPACITY);
		}
		getFactory().getEnvironment().getModelChangeListener().onListAdd(this, CtRole.TYPE_PARAMETER, this.formalCtTypeParameters, formalTypeParameter);
		formalTypeParameter.setParent(this);
		formalCtTypeParameters.add(formalTypeParameter);
		return this;
	}

	@Override
	public boolean removeFormalCtTypeParameter(CtTypeParameter formalTypeParameter) {
		if (formalCtTypeParameters == CtElementImpl.<CtTypeParameter>emptyList()) {
			return false;
		}
		getFactory().getEnvironment().getModelChangeListener().onListDelete(this, CtRole.TYPE_PARAMETER, formalCtTypeParameters, formalCtTypeParameters.indexOf(formalTypeParameter), formalTypeParameter);
		return formalCtTypeParameters.remove(formalTypeParameter);
	}

	@Override
	public Set<ModifierKind> getModifiers() {
		return modifierHandler.getModifiers();
	}

	@Override
	public boolean hasModifier(ModifierKind modifier) {
		return getModifiers().contains(modifier);
	}

	@Override
	public CtMethodImpl<T> setModifiers(Set<ModifierKind> modifiers) {
		modifierHandler.setModifiers(modifiers);
		return this;
	}

	@Override
	public CtMethodImpl<T> addModifier(ModifierKind modifier) {
		modifierHandler.addModifier(modifier);
		return this;
	}

	@Override
	public CtMethodImpl<T> removeModifier(ModifierKind modifier) {
		modifierHandler.removeModifier(modifier);
		return this;
	}

	@Override
	public CtMethodImpl<T> setVisibility(ModifierKind visibility) {
		modifierHandler.setVisibility(visibility);
		return this;
	}

	@Override
	public ModifierKind getVisibility() {
		return modifierHandler.getVisibility();
	}

	@Override
	public Set<CtExtendedModifier> getExtendedModifiers() {
		return this.modifierHandler.getExtendedModifiers();
	}

	@Override
	public CtMethodImpl<T> setExtendedModifiers(Set<CtExtendedModifier> extendedModifiers) {
		this.modifierHandler.setExtendedModifiers(extendedModifiers);
		return  this;
	}

	@Override
	public boolean isOverriding(CtMethod<?> superMethod) {
		return new ClassTypingContext(getDeclaringType()).isOverriding(this, superMethod);
	}

	@MetamodelPropertyField(role = CtRole.IS_SHADOW)
	boolean isShadow;

	@Override
	public boolean isShadow() {
		return isShadow;
	}

	@Override
	public CtMethodImpl<T> setShadow(boolean isShadow) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.IS_SHADOW, isShadow, this.isShadow);
		this.isShadow = isShadow;
		return this;
	}

	@Override
	public CtMethod<T> clone() {
		return (CtMethod<T>) super.clone();
	}

	@Override
	public Collection<CtMethod<?>> getTopDefinitions() {
		List<CtMethod<?>> s = new ArrayList<>();

		// first collect potential declarations of this method in the type hierarchy
		ClassTypingContext context = new ClassTypingContext(this.getDeclaringType());
		getDeclaringType().map(new AllTypeMembersFunction(CtMethod.class)).forEach((CtMethod<?> m) -> {
			if (m != this && context.isOverriding(this, m)) {
				s.add(m);
			}
		});

		// now removing the intermediate methods for which there exists a definition upper in the hierarchy
		List<CtMethod<?>> finalMeths = new ArrayList<>(s);
		for (CtMethod m1 : s) {
			boolean m1IsIntermediate = false;
			for (CtMethod m2 : s) {
				if (context.isOverriding(m1, m2)) {
					m1IsIntermediate = true;
				}
			}
			if (!m1IsIntermediate) {
				finalMeths.add(m1);
			}
		}
		return finalMeths;
	}

	@Override
	public boolean isPublic() {
		return this.modifierHandler.isPublic();
	}

	@Override
	public boolean isPrivate() {
		return this.modifierHandler.isPrivate();
	}

	@Override
	public boolean isProtected() {
		return this.modifierHandler.isProtected();
	}

	@Override
	public boolean isFinal() {
		return this.modifierHandler.isFinal();
	}

	@Override
	public boolean isStatic() {
		return this.modifierHandler.isStatic();
	}

	@Override
	public boolean isAbstract() {
		return this.modifierHandler.isAbstract();
	}

	@Override
	public CtMethod<?> copyMethod() {
		return Refactoring.copyMethod(this);
	}
}
