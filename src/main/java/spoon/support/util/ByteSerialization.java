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
		ByteArrayInputStream bi = new ByteArrayInputStream( serializedObject);
		ObjectInputStream si = new ObjectInputStream(bi);
		objInput = (Object) si.readObject();
		si.close();
		return objInput;
	}
	
}
