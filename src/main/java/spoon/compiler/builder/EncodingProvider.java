/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.compiler.builder;

import spoon.compiler.SpoonFile;
import java.nio.charset.Charset;

public interface EncodingProvider {

	/**
	* User-defined function, which is used to detect encoding for each file
	*/
	Charset detectEncoding(SpoonFile file, byte[] fileBytes);
}
