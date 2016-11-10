package spoon.test.serializable;

import org.junit.Test;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtPackage;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static spoon.testing.utils.ModelUtils.build;

public class SerializableTest {

	@Test
	public void testSerialCtStatement() throws Exception {
		Factory factory = new FactoryImpl(new DefaultCoreFactory(), new StandardEnvironment());
		CtStatement sta2 = (factory).Code()
				.createCodeSnippetStatement("String hello =\"t1\"; System.out.println(hello)").compile();

		byte[] ser = ByteSerialization.serialize(sta2);
		CtStatement des = (CtStatement) ByteSerialization.deserialize(ser);

		String sigBef = sta2.getShortRepresentation();
		String sigAf = des.getShortRepresentation();

		CtType<?> typeBef = sta2.getParent(CtType.class);
		assertNotNull(typeBef);

		assertEquals(sigBef, sigAf);

		des.setFactory(factory);
		String toSBef = sta2.toString();
		String toSgAf = des.toString();

		assertEquals(toSBef, toSgAf);

		CtType<?> typeDes = des.getParent(CtType.class);
		assertNotNull(typeDes);
		//After deserialization, getDeclaringType throws an exception
		CtType<?> decl =  typeDes.getDeclaringType();
		assertNull(decl);

		CtPackage parentOriginal = (CtPackage) typeBef.getParent();
		CtPackage parentDeser = (CtPackage) typeDes.getParent();

		assertEquals(CtPackage.TOP_LEVEL_PACKAGE_NAME,parentOriginal.getSimpleName());

		assertEquals(CtPackage.TOP_LEVEL_PACKAGE_NAME,parentDeser.getSimpleName());

	}

	@Test
	public void testSerialFile() throws Exception {
		CtType<?> type = build("spoon.test.serializable", "Dummy");
		byte[] ser = ByteSerialization.serialize(type);
		CtType<?> des = (CtType<?>) ByteSerialization.deserialize(ser);
	}

	@Test
	public void testSerializationModelStreamer() throws Exception {
		Factory factory = build("spoon.test.serializable", "Dummy").getFactory();

		ByteArrayOutputStream outstr = new ByteArrayOutputStream();

		new SerializationModelStreamer().save(factory, outstr);


		Factory loadedFactory = new SerializationModelStreamer().load(new ByteArrayInputStream(outstr.toByteArray()));

		assertFalse(factory.Type().getAll().isEmpty());
		assertFalse(loadedFactory.Type().getAll().isEmpty());
		assertEquals(factory.getModel().getRootPackage(), loadedFactory.getModel().getRootPackage());
	}
}
