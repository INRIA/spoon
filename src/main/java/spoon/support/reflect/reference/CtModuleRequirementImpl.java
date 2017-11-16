package spoon.support.reflect.reference;

import spoon.reflect.reference.CtModuleRequirement;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.HashSet;
import java.util.Set;

public class CtModuleRequirementImpl extends CtModuleReferenceImpl implements CtModuleRequirement {
    Set<RequiresModifier> requiresModifiers = CtElementImpl.emptySet();

    public CtModuleRequirementImpl() {
        super();
    }

    @Override
    public Set<RequiresModifier> getRequiresModifiers() {
        return this.requiresModifiers;
    }

    @Override
    public <T extends CtModuleRequirement> T setRequiresModifiers(Set<RequiresModifier> requiresModifiers) {
        if (requiresModifiers == null || requiresModifiers.isEmpty()) {
            this.requiresModifiers = CtElementImpl.emptySet();
            return (T) this;
        }

        if (this.requiresModifiers == CtElementImpl.<RequiresModifier>emptySet()) {
            this.requiresModifiers = new HashSet<>();
        }
        this.requiresModifiers.clear();
        this.requiresModifiers.addAll(requiresModifiers);

        return (T) this;
    }
}
