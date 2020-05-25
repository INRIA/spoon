/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.declaration;

/**
 * Represents a directive of a {@link CtModule}
 *
 * The directives of a module declaration specify the module's dependences on other modules {@link CtModuleRequirement},
 * the packages it makes available to other modules {@link CtPackageExport},
 * the services it consumes {@link CtUsedService},
 * and the services it provides {@link CtProvidedService}.
 */
public interface CtModuleDirective extends CtElement {


}
