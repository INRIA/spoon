package spoon.reflect.declaration;

import spoon.reflect.reference.CtTypeReference;

import java.util.Collection;
import java.util.Set;

// TODO docs

public interface CtSealable {

	Set<CtTypeReference<?>> getPermittedTypes();

	CtSealable setPermittedTypes(Collection<CtTypeReference<?>> permittedTypes);

	CtSealable addPermittedType(CtTypeReference<?> type);

	CtSealable removePermittedType(CtTypeReference<?> type);
}
