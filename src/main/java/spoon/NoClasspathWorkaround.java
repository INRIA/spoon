/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon;

/**
 * This annotation is used to mark a workaround for the lack of a correct classpath so called noclasspathmode.
 * <p>
 * Workarounds for missing informations are marked with this annotation. These methods are not part of the Spoon API and best effort.
 * With any new jdt version the workaround can be removed or no longer working.
 */
public @interface NoClasspathWorkaround {
}
