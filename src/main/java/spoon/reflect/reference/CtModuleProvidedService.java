package spoon.reflect.reference;

import java.util.Set;

public interface CtModuleProvidedService extends CtTypeReference {

    Set<CtTypeReference> getProvidingTypes();

    <T extends CtModuleProvidedService> T setProvidingTypes(Set<CtTypeReference> providingTypes);
}
