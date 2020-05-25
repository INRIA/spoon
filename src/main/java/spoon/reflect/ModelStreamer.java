/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import spoon.compiler.Environment;
import spoon.reflect.factory.Factory;
import spoon.support.CompressionType;

/**
 * This interface defines the protocol to save and load a factory and it's
 * associated model through output and input streams.
 */
public interface ModelStreamer {

	/**
	 * Saves a factory (and all its associated Java program elements).
	 * Stream is GZIP compressed by default, see {@link Environment#setCompressionType(spoon.support.CompressionType)}
	 *
	 * @param f
	 * 		the factory to be save
	 * @param out
	 * 		the used output stream
	 * @throws IOException
	 * 		if some IO error occurs
	 */
	void save(Factory f, OutputStream out) throws IOException;

	/**
	 * Loads a factory (and all its associated Java program elements).
	 * Tries to decompress the file given the available {@link CompressionType}
	 *
	 * @param in
	 * 		the used input stream
	 * @return the loaded factory
	 * @throws IOException
	 * 		if some IO error occurs
	 */
	Factory load(InputStream in) throws IOException;

}
