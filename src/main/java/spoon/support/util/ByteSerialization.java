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
package spoon.support.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ByteSerialization {

	private ByteSerialization() { }

	public static byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		try (ObjectOutputStream so = new ObjectOutputStream(bo)) {
			so.writeObject(obj);
			so.flush();
			return bo.toByteArray();
		}
	}

	public static Object deserialize(byte[] serializedObject) throws Exception {
		ByteArrayInputStream bi = new ByteArrayInputStream(serializedObject);
		try (ObjectInputStream si = new ObjectInputStream(bi)) {
			return si.readObject();
		}
	}
}
