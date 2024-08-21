/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.experimental;

import spoon.support.Experimental;
import spoon.support.Internal;

/**
 * This annotation is used to mark a workaround for the lack of a correct classpath so called noclasspathmode.
 * <p>
 * Workarounds for missing informations are marked with this annotation. These methods are not part of the Spoon API and best effort.
 * With any new jdt version the workaround can be removed or no longer working.
 */
@Experimental
@Internal
public @interface NoClasspathWorkaround {
	/**
	 * The reason why this workaround is needed. This is used for documentation purposes.
	 * A link to the originale issue is sufficient.
	 * @return  the reason why this workaround is needed
	 */
	String reason();
}
