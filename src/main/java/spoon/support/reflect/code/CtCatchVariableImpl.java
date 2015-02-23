package spoon.support.reflect.code;

import spoon.delegate.ModifiableDelegate;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.delegate.ModifiableDelegateImpl;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class CtCatchVariableImpl<T> extends CtCodeElementImpl implements CtCatchVariable<T> {
	private static final long serialVersionUID = 1L;

	CtExpression<T> defaultExpression;

	String name;

	CtTypeReference<T> type;

	List<CtTypeReference<?>> types = EMPTY_LIST();

	ModifiableDelegate modifiableDelegate = new ModifiableDelegateImpl();

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
			types = new ArrayList<CtTypeReference<?>>();
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
		return modifiableDelegate.getModifiers();
	}

	@Override
	public boolean hasModifier(ModifierKind modifier) {
		return modifiableDelegate.hasModifier(modifier);
	}

	@Override
	public void setModifiers(Set<ModifierKind> modifiers) {
		modifiableDelegate.setModifiers(modifiers);
	}

	@Override
	public boolean addModifier(ModifierKind modifier) {
		return modifiableDelegate.addModifier(modifier);
	}

	@Override
	public boolean removeModifier(ModifierKind modifier) {
		return modifiableDelegate.removeModifier(modifier);
	}

	@Override
	public void setVisibility(ModifierKind visibility) {
		modifiableDelegate.setVisibility(visibility);
	}

	@Override
	public ModifierKind getVisibility() {
		return modifiableDelegate.getVisibility();
	}
}
