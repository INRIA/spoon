/**
 * Copyright (C) 2006-2015 INRIA and contributors
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
package spoon.reflect;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import spoon.reflect.factory.Factory;

/**
 * This interface defines the protocol to save and load a factory and it's
 * associated model through output and input streams.
 */
public interface ModelStreamer {

	/**
	 * Saves a factory (and all its associated Java program elements).
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
	 *
	 * @param in
	 * 		the used input stream
	 * @return the loaded factory
	 * @throws IOException
	 * 		if some IO error occurs
	 */
	Factory load(InputStream in) throws IOException;

}
