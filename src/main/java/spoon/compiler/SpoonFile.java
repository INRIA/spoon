/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.compiler;

import org.apache.commons.io.IOUtils;
import spoon.SpoonException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * This interface represents files that can be used as resources for the Spoon
 * compiler.
 */
public interface SpoonFile extends SpoonResource {

	/**
	 * Gets the file content as a stream.
	 */
	InputStream getContent();

	/**
	 * True if a Java source code file.
	 */
	boolean isJava();

	/**
	 * Tells if this file is an actual file (not a virtual file that holds
	 * in-memory contents).
	 *
	 * @return
	 */
	boolean isActualFile();

	/**
	 * Gets the file content as a char array, considering encoding or encoding
	 * provider.
	 */
	default char[] getContentChars(Environment env) {
		byte[] bytes;
		try (InputStream contentStream = getContent()) {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			IOUtils.copy(contentStream, outputStream);
			bytes = outputStream.toByteArray();
		} catch (IOException e) {
			throw new SpoonException(e);
		}
		if (env.getEncodingProvider() == null) {
			return new String(bytes, env.getEncoding()).toCharArray();
		} else {
			Charset encoding = env.getEncodingProvider().detectEncoding(this, bytes);
			return new String(bytes, encoding).toCharArray();
		}
	}
}
