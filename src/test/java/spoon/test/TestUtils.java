package spoon.test;

import spoon.Launcher;
import spoon.compiler.SpoonCompiler;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;

import java.io.File;
import java.io.IOException;

public class TestUtils {

	public static Factory createFactory() {		
		return new FactoryImpl(new DefaultCoreFactory(),
				new StandardEnvironment());
	}
	
	public static <T extends CtType<?>> T build(String packageName,
			String className) throws Exception {
		SpoonCompiler comp = new Launcher().createCompiler();
		comp.addInputSources(SpoonResourceHelper.resources("./src/test/java/"
				+ packageName.replace('.', '/') + "/" + className + ".java"));
		comp.build();
		return comp.getFactory().Package().get(packageName).getType(className);
	}

	public static <T extends CtType<?>> T build(String packageName,
			String className, final Factory f) throws Exception {
		Launcher launcher = new Launcher() {
			@Override
			public Factory createFactory() {
				return f;
			}
		};
		SpoonCompiler comp = launcher.createCompiler();
		comp.addInputSources(SpoonResourceHelper.resources("./src/test/java/"
				+ packageName.replace('.', '/') + "/" + className + ".java"));
		comp.build();
		return comp.getFactory().Package().get(packageName).getType(className);
	}

	public static Factory build(Class<?>... classesToBuild) throws Exception {
		SpoonCompiler comp = new Launcher().createCompiler();
        for (Class<?> classToBuild : classesToBuild) {
		comp.addInputSources(SpoonResourceHelper.resources("./src/test/java/"
				+ classToBuild.getName().replace('.', '/') + ".java"));
        }
		comp.build();
		return comp.getFactory();
	}

	public static void canBeBuild(File outputDirectoryFile, int complianceLevel) throws IOException {
		final Launcher launcher = new Launcher();
		final Factory factory = launcher.createFactory();
		factory.getEnvironment().setComplianceLevel(complianceLevel);
		final SpoonCompiler compiler = launcher.createCompiler(factory);
		compiler.addInputSource(outputDirectoryFile);
		try {
			compiler.build();
		} catch (Exception e) {
			throw new AssertionError("Can't compile " + outputDirectoryFile.getName(), e);
		}
	}

	public static void canBeBuild(String outputDirectory, int complianceLevel) throws IOException {
		canBeBuild(new File(outputDirectory), complianceLevel);
	}
}
