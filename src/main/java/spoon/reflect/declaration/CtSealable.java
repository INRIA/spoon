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

	@PropertyGetter(role = CtRole.TYPE_REF)
	Set<CtTypeReference<?>> getPermittedTypes();

	@PropertySetter(role = CtRole.TYPE_REF)
	CtSealable setPermittedTypes(Collection<CtTypeReference<?>> permittedTypes);

	@PropertySetter(role = CtRole.TYPE_REF)
	CtSealable addPermittedType(CtTypeReference<?> type);

	@PropertySetter(role = CtRole.TYPE_REF)
	CtSealable removePermittedType(CtTypeReference<?> type);
}
