package spoon.reflect.reference;

import java.util.Set;

public interface CtModuleRequirement extends CtModuleReference {

    enum RequiresModifier {
        STATIC, TRANSITIVE;
    }

    Set<RequiresModifier> getRequiresModifiers();

    <T extends CtModuleRequirement> T setRequiresModifiers(Set<RequiresModifier> requiresModifiers);
}
