/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
/**
 * This package provides support for the sniper mode: only the transformed part of classes is rewritten to disk. All the other code is kept as is (formatting, newlines) as the original code.
 *
 * Public class: {@link spoon.support.sniper.SniperJavaPrettyPrinter}, to use it:
 * <pre>
 *     	launcher.getEnvironment().setPrettyPrinterCreator(() -&gt; {
 * 			return new SniperJavaPrettyPrinter(launcher.getEnvironment());}
 * 		);
 * </pre>
 * See https://github.com/INRIA/spoon/issues/1284
 */
package spoon.support.sniper;

