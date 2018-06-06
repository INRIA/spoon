package spoon.test.serializable;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.Test;

import spoon.Launcher;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.support.SerializationModelStreamer;

public class SourcePositionTest {

	private static Factory loadFactory(File file) throws IOException {
		return new SerializationModelStreamer().load(new FileInputStream(file));
	}

	private static void saveFactory(Factory factory, File file) throws IOException {
		ByteArrayOutputStream outstr = new ByteArrayOutputStream();
		new SerializationModelStreamer().save(factory, outstr);
		OutputStream fileStream = new FileOutputStream(file);
		outstr.writeTo(fileStream);
	}

	@Test
	public void testSourcePosition() throws IOException {
		File modelFile = new File("./src/test/resources/serialization/model");
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/serialization/SomeClass.java");
		launcher.buildModel();

		Factory factory = launcher.getFactory();

		saveFactory(factory, modelFile);

		Factory factoryFromFile = loadFactory(modelFile);

		CtType<?> type = factory.Type().get("SomeClass");
		CtType<?> typeFromFile = factoryFromFile.Type().get("SomeClass");

		// Serialized model should have same valid source positions as the original model
		assertTrue(type.getPosition().getFile().equals(typeFromFile.getPosition().getFile()));
		assertTrue(type.getPosition().getLine() == typeFromFile.getPosition().getLine());
		assertTrue(type.getPosition().getColumn() == typeFromFile.getPosition().getColumn());

		CtField<?> elem1 = type.getField("a");
		CtField<?> elem2 = typeFromFile.getField("a");
		assertTrue(elem1.getPosition().getFile().equals(elem2.getPosition().getFile()));
	}
}
