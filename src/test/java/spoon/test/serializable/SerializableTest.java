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
package spoon.test.serializable;

import org.junit.Test;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.DefaultCoreFactory;
import spoon.support.SerializationModelStreamer;
import spoon.support.StandardEnvironment;
import spoon.support.util.ByteSerialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static spoon.testing.utils.ModelUtils.build;

public class SerializableTest {

	@Test
	public void testSerialCtStatement() throws Exception {
		// contract: one can also serialize snippets
		Factory factory = new FactoryImpl(new DefaultCoreFactory(), new StandardEnvironment());
		CtStatement sta2 = (factory).Code()
				.createCodeSnippetStatement("String hello =\"t1\"; System.out.println(hello)").compile();

		byte[] ser = ByteSerialization.serialize(sta2);
		CtStatement deserializedSta2 = (CtStatement) ByteSerialization.deserialize(ser);

		String sigBef = sta2.getShortRepresentation();
		String sigAf = deserializedSta2.getShortRepresentation();

		CtType<?> typeBef = sta2.getParent(CtType.class);

		// sta2 comes from a snippet, and snippets have no parent (#2318)
		assertNull(typeBef);

		assertEquals(sigBef, sigAf);

		deserializedSta2.setFactory(factory);
		String toSBef = sta2.toString();
		String toSgAf = deserializedSta2.toString();

		assertEquals(toSBef, toSgAf);

		CtType<?> typeDes = deserializedSta2.getParent(CtType.class);

		// typeDes comes from a serialized snippet, and snippets have no parent (#2318)
		assertNull(typeDes);
		assertFalse(deserializedSta2.isParentInitialized());
	}

	@Test
	public void testSerialFile() throws Exception {
		CtType<?> type = build("spoon.test.serializable.testclasses", "Dummy");
		byte[] ser = ByteSerialization.serialize(type);
		CtType<?> des = (CtType<?>) ByteSerialization.deserialize(ser);
	}

	@Test
	public void testSerializationModelStreamer() throws Exception {
		Factory factory = build("spoon.test.serializable.testclasses", "Dummy").getFactory();
		ByteArrayOutputStream outstr = new ByteArrayOutputStream();
		new SerializationModelStreamer().save(factory, outstr);
		Factory loadedFactory = new SerializationModelStreamer().load(new ByteArrayInputStream(outstr.toByteArray()));

		assertFalse(factory.Type().getAll().isEmpty());
		assertFalse(loadedFactory.Type().getAll().isEmpty());
		assertEquals(factory.getModel().getRootPackage(), loadedFactory.getModel().getRootPackage());
		//contract: each element of loaded model has same factory
		for (CtType type : loadedFactory.Type().getAll()) {
			assertSame(loadedFactory, type.getFactory());
			assertSame(loadedFactory, type.getPosition().getCompilationUnit().getFactory());
		}
	}
}
