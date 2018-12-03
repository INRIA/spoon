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
