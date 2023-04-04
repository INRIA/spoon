/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.sniper.internal;

/** represents whether a {@link spoon.support.sniper.internal.SourceFragment} has been modified, and should be reprinted as is or with the normal pretty-printer */
public enum ModificationStatus {
	UNKNOWN,
	MODIFIED,
	NOT_MODIFIED;

	public static ModificationStatus fromBoolean(Boolean b) {
		if (b) {
			return MODIFIED;
		}
		return NOT_MODIFIED;
	}

	public boolean toBoolean() {
		if (this == MODIFIED) {
			return true;
		}
		if (this == NOT_MODIFIED) {
			return false;
		}
		throw new IllegalStateException();
	}
}
