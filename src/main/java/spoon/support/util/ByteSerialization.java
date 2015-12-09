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
package spoon.support.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ByteSerialization {

	public static byte[] serialize(Object obj) throws IOException {

		byte[] serializedObject = null;
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		ObjectOutputStream so = new ObjectOutputStream(bo);
		so.writeObject(obj);
		so.flush();
		serializedObject = bo.toByteArray();
		so.close();
		return serializedObject;
	}

	public static Object deserialize(byte[] serializedObject) throws Exception {

		Object objInput = null;
		ByteArrayInputStream bi = new ByteArrayInputStream(serializedObject);
		ObjectInputStream si = new ObjectInputStream(bi);
		objInput = si.readObject();
		si.close();
		return objInput;
	}

}
