package spoon.support.reflect.code;

import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtCatchVariableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class CtCatchVariableImpl<T> extends CtCodeElementImpl implements CtCatchVariable<T> {
	private static final long serialVersionUID = 1L;

	CtExpression<T> defaultExpression;

	Set<ModifierKind> modifiers = EMPTY_SET();

	String name;

	CtTypeReference<T> type;

	List<CtTypeReference<?>> types = EMPTY_LIST();

	public boolean addModifier(ModifierKind modifier) {
		setMutable();
		return modifiers.add(modifier);
	}

	public boolean removeModifier(ModifierKind modifier) {
		setMutable();
		return modifiers.remove(modifier);
	}

	public void accept(CtVisitor visitor) {
		visitor.visitCtCatchVariable(this);
	}

	public CtExpression<T> getDefaultExpression() {
		return defaultExpression;
	}

	public Set<ModifierKind> getModifiers() {
		return modifiers;
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

	public ModifierKind getVisibility() {
		if (getModifiers().contains(ModifierKind.PUBLIC)) {
			return ModifierKind.PUBLIC;
		}
		if (getModifiers().contains(ModifierKind.PROTECTED)) {
			return ModifierKind.PROTECTED;
		}
		if (getModifiers().contains(ModifierKind.PRIVATE)) {
			return ModifierKind.PRIVATE;
		}
		return null;
	}

	public boolean hasModifier(ModifierKind modifier) {
		return modifiers.contains(modifier);
	}

	public void setDefaultExpression(CtExpression<T> defaultExpression) {
		this.defaultExpression = defaultExpression;
		this.defaultExpression.setParent(this);
	}

	public void setModifiers(Set<ModifierKind> modifiers) {
		this.modifiers = modifiers;
	}

	public void setSimpleName(String simpleName) {
		this.name = simpleName;
	}

	public void setType(CtTypeReference<T> type) {
		this.type = type;
	}

	private void setMutable() {
		if (modifiers == CtElementImpl.<ModifierKind>EMPTY_SET()) {
			modifiers = new TreeSet<ModifierKind>();
		}
	}

	public void setVisibility(ModifierKind visibility) {
		setMutable();
		getModifiers().remove(ModifierKind.PUBLIC);
		getModifiers().remove(ModifierKind.PROTECTED);
		getModifiers().remove(ModifierKind.PRIVATE);
		getModifiers().add(visibility);
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
}
