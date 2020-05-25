/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
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
