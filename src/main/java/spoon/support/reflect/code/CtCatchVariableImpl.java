package spoon.support.reflect.code;

import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.*;

import static spoon.reflect.ModelElementContainerDefaultCapacities.CATCH_VARIABLE_MULTI_TYPES_CONTAINER_DEFAULT_CAPACITY;

public class CtCatchVariableImpl<T> extends CtCodeElementImpl implements CtCatchVariable<T> {
	private static final long serialVersionUID = 1L;

	CtExpression<T> defaultExpression;

	String name;

	CtTypeReference<T> type;

	List<CtTypeReference<?>> types = EMPTY_LIST();

	Set<ModifierKind> modifiers = CtElementImpl.EMPTY_SET();

	public void accept(CtVisitor visitor) {
		visitor.visitCtCatchVariable(this);
	}

	public CtExpression<T> getDefaultExpression() {
		return defaultExpression;
	}

	public CtCatchVariableReference<T> getReference() {
		return getFactory().Code().createCatchVariableReference(this);
	}

	public String getSimpleName() {
		return name;
	}

	public CtTypeReference<T> getType() {
		return type;
	}

	public void setDefaultExpression(CtExpression<T> defaultExpression) {
		this.defaultExpression = defaultExpression;
		this.defaultExpression.setParent(this);
	}

	public void setSimpleName(String simpleName) {
		this.name = simpleName;
	}

	public void setType(CtTypeReference<T> type) {
		this.type = type;
	}

	@Override
	public boolean addMultiType(CtTypeReference<?> ref) {
		if (types == CtElementImpl.<CtTypeReference<?>>EMPTY_LIST()) {
			types = new ArrayList<CtTypeReference<?>>(
					CATCH_VARIABLE_MULTI_TYPES_CONTAINER_DEFAULT_CAPACITY);
		}
		return types.add(ref);
	}

	@Override
	public boolean removeMultiType(CtTypeReference<?> ref) {
		return types.remove(ref);
	}

	@Override
	public List<CtTypeReference<?>> getMultiTypes() {
		return types;
	}

	@Override
	public Set<ModifierKind> getModifiers() {
		return modifiers;
	}

	@Override
	public boolean hasModifier(ModifierKind modifier) {
		return getModifiers().contains(modifier);
	}

	@Override
	public void setModifiers(Set<ModifierKind> modifiers) {
		this.modifiers = modifiers;
	}

	@Override
	public boolean addModifier(ModifierKind modifier) {
		if (modifiers == CtElementImpl.<ModifierKind> EMPTY_SET()) {
			this.modifiers = EnumSet.noneOf(ModifierKind.class);
		}
		return modifiers.add(modifier);
	}

	@Override
	public boolean removeModifier(ModifierKind modifier) {
		return !modifiers.isEmpty() && modifiers.remove(modifier);
	}

	@Override
	public void setVisibility(ModifierKind visibility) {
		if (modifiers == CtElementImpl.<ModifierKind> EMPTY_SET()) {
			this.modifiers = EnumSet.noneOf(ModifierKind.class);
		}
		getModifiers().remove(ModifierKind.PUBLIC);
		getModifiers().remove(ModifierKind.PROTECTED);
		getModifiers().remove(ModifierKind.PRIVATE);
		getModifiers().add(visibility);
	}

	@Override
	public ModifierKind getVisibility() {
		if (getModifiers().contains(ModifierKind.PUBLIC))
			return ModifierKind.PUBLIC;
		if (getModifiers().contains(ModifierKind.PROTECTED))
			return ModifierKind.PROTECTED;
		if (getModifiers().contains(ModifierKind.PRIVATE))
			return ModifierKind.PRIVATE;
		return null;
	}
}
