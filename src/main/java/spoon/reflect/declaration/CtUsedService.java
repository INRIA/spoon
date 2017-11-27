package spoon.reflect.declaration;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;

/**
 * Represents the declaration of a used service in a module
 *
 * Example:
 *
 * <pre>
 *     uses java.logging.Logger;
 * </pre>
 */
public interface CtUsedService extends CtModuleMember {
	@PropertyGetter(role = CtRole.SERVICE_TYPE)
	CtTypeReference getServiceType();

	@PropertySetter(role = CtRole.SERVICE_TYPE)
	<T extends CtUsedService> T setServiceType(CtTypeReference providingType);

	@Override
	CtUsedService clone();
}
