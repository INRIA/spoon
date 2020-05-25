/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.annotations.PropertySetter;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtTypeReference;

/**
 * Represents the declaration of a used service in a {@link CtModule}
 *
 * The uses directive specifies a service for which the current module may discover providers via java.util.ServiceLoader.
 * The service must be a class type, an interface type, or an annotation type. It is a compile-time error if a uses directive specifies an enum type as the service.
 * The service may be declared in the current module or in another module. If the service is not declared in the current module, then the service must be accessible to code in the current module, or a compile-time error occurs.
 * It is a compile-time error if more than one uses directive in a module declaration specifies the same service.
 *
 * Example:
 *
 * <pre>
 *     uses java.logging.Logger;
 * </pre>
 */
public interface CtUsedService extends CtModuleDirective {
	@PropertyGetter(role = CtRole.SERVICE_TYPE)
	CtTypeReference getServiceType();

	@PropertySetter(role = CtRole.SERVICE_TYPE)
	<T extends CtUsedService> T setServiceType(CtTypeReference providingType);

	@Override
	CtUsedService clone();
}
