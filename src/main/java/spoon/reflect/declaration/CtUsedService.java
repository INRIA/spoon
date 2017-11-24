package spoon.reflect.declaration;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;

public interface CtUsedService extends CtElement {
	@PropertyGetter(role = CtRole.SERVICE_TYPE)
	CtTypeReference getServiceType();

	@PropertySetter(role = CtRole.SERVICE_TYPE)
	<T extends CtProvidedService> T setServiceType(CtTypeReference providingType);

	@Override
	CtUsedService clone();
}
