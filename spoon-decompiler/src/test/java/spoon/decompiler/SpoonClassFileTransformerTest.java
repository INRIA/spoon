package spoon.decompiler;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class SpoonClassFileTransformerTest {

	final File RESOURCES_DIR = new File("./src/test/resources/agent");
	final File CLASSES_DIR = new File(RESOURCES_DIR, "classes");

	final File TMP_DIR = new File("./tmp");
	final File DECOMPILED_DIR = new File(TMP_DIR, "spoon-decompiled");
	final File CACHE_DIR = new File(TMP_DIR, "spoon-cache");
	final File RECOMPILED_DIR = new File(TMP_DIR, "spoon-recompiled");

	private static class TransformingClassLoader extends ClassLoader {
		private String classPath;
		private ClassFileTransformer transformer;

		public TransformingClassLoader(ClassFileTransformer transformer, String classPath) {
			this.transformer = transformer;
			this.classPath = classPath;
		}

		@Override
		public Class<?> loadClass(String name) throws ClassNotFoundException {
			try {
				byte[] original =  Files.readAllBytes(Paths.get(classPath + "/" + name + ".class"));
				byte[] byteBuffer = transformer.transform(this, name,null, null, original);
				return defineClass(name.replace("/", "."), byteBuffer, 0, byteBuffer.length);
			} catch (IOException | IllegalClassFormatException e) {
			}
			return super.loadClass(name);
		}

		@Override
		public URL getResource(String name) {
			try {
				return new File(classPath + "/" + name).toPath().toUri().toURL();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			return null;
		}
	}


	@Test
	public void testClassFileTransform() throws ClassNotFoundException,
			IllegalAccessException,
			InvocationTargetException,
			InstantiationException,
			NoSuchMethodException,
			IOException {
		testClassFileTransform(null);
	}


	@Test
	public void testClassFileTransformWithProcyon() throws ClassNotFoundException,
			IllegalAccessException,
			InvocationTargetException,
			InstantiationException,
			NoSuchMethodException,
			IOException {
		testClassFileTransform(new ProcyonDecompiler());
	}


	@Test
	public void testClassFileTransformWithFernflower() throws ClassNotFoundException,
			IllegalAccessException,
			InvocationTargetException,
			InstantiationException,
			NoSuchMethodException,
			IOException {
		testClassFileTransform(new FernflowerDecompiler());
	}

	public void transform(CtType type) {
		//Decompiler might create a default constructor with this.transformed=false... or not
		//if it does, add a statement at the end of the constructor if not change the default expression
		if(type instanceof CtClass) {
			if(((CtClass) type).getConstructor().getBody().getStatements().size() <= 1) {
				type.getField("transformed").setAssignment(type.getFactory().createCodeSnippetExpression("true"));
			} else {
				((CtClass) type).getConstructor().getBody().insertEnd(type.getFactory().createCodeSnippetStatement("this.transformed=true"));
			}
		}
	}

	public void testClassFileTransform(Decompiler decompiler) throws ClassNotFoundException,
			IllegalAccessException,
			InvocationTargetException,
			InstantiationException,
			NoSuchMethodException,
			IOException {
		//Setup temporary directories
		FileUtils.deleteDirectory(TMP_DIR);
		TMP_DIR.mkdir();
		DECOMPILED_DIR.mkdir();
		CACHE_DIR.mkdir();
		RECOMPILED_DIR.mkdir();
		//type -> type.getField("transformed").setAssignment(type.getFactory().createCodeSnippetExpression("true")),

		//Create a SpoonClassFileTransformer
		SpoonClassFileTransformer transformer = new SpoonClassFileTransformer(
				s -> s.startsWith("se/kth/castor"),
				type -> transform(type),
				DECOMPILED_DIR.getPath(),
				CACHE_DIR.getPath(),
				RECOMPILED_DIR.getPath(),
				decompiler
		);

		//Class loaded by cl should be transformed
		TransformingClassLoader cl = new TransformingClassLoader(transformer, CLASSES_DIR.getPath());

		//Load a class
		Class<?> subjectClass = cl.loadClass("se/kth/castor/TransformMe");

		//Instantiate it and call method isTransformed
		Constructor<?> constructor = subjectClass.getConstructor();
		Object subject = constructor.newInstance();
		Method isTransformed = subjectClass.getMethod("isTransformed");
		Object transformed = isTransformed.invoke(subject, new Object[]{});

		//Contract: field transformed assignment has been change from false to true on class se.kth.castor.TransformMe
		assertTrue((Boolean) transformed);
		FileUtils.deleteDirectory(TMP_DIR);
	}
}
