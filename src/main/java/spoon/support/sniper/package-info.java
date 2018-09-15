/**
 * Copyright (C) 2006-2018 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
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

