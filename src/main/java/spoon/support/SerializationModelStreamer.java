/* 
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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

package spoon.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import spoon.reflect.Factory;
import spoon.reflect.ModelStreamer;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.CtScanner;

/**
 * This class provides a regular Java serialization-based implementation of the
 * model streamer.
 */
public class SerializationModelStreamer implements ModelStreamer {

	/**
	 * Default constructor.
	 */
	public SerializationModelStreamer() {
	}

	public void save(Factory f, OutputStream out) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(f);
		oos.close();
	}

	public Factory load(InputStream in) throws IOException {
		try {
			ObjectInputStream ois = new ObjectInputStream(in);
			final Factory f = (Factory) ois.readObject();
			new CtScanner() {
				public void enter(CtElement e) {
					e.setFactory(f);
					super.enter(e);
				}

				@Override
				protected void enterReference(CtReference e) {
					e.setFactory(f);
					super.enterReference(e);
				}
			}.scan(f.Package().getAll());
			ois.close();
			return f;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
	}

}
