/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
/**
 * Deprecated assertion classes for Spoon elements.
 *
 * <p>Use {@code spoon.testing.assertions.SpoonAssertions} instead, which provides
 * AssertJ-based fluent assertions for all Spoon types. Example:
 * <pre>
 *   import static spoon.testing.assertions.SpoonAssertions.assertThat;
 *   assertThat(myCtClass).isEqualTo(otherCtClass);
 * </pre>
 *
 * @deprecated Use {@code spoon.testing.assertions.SpoonAssertions} instead.
 */
@Deprecated(since = "11", forRemoval = true)
package spoon.testing;
