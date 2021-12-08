package spoon.reflect.declaration;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;

import java.util.Collection;
import java.util.Set;

// TODO docs
// TODO return type?

public interface CtSealable {

	@PropertyGetter(role = CtRole.PERMITTED_TYPE)
	Set<CtTypeReference<?>> getPermittedTypes();

	@PropertySetter(role = CtRole.PERMITTED_TYPE)
	CtSealable setPermittedTypes(Collection<CtTypeReference<?>> permittedTypes);

	@PropertySetter(role = CtRole.PERMITTED_TYPE)
	CtSealable addPermittedType(CtTypeReference<?> type);

	@PropertySetter(role = CtRole.PERMITTED_TYPE)
	CtSealable removePermittedType(CtTypeReference<?> type);
}
