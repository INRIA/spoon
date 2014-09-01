package spoon.test;

import spoon.Launcher;
import spoon.compiler.SpoonCompiler;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;

public class TestUtils {

	public static Factory createFactory() {		
		return new FactoryImpl(new DefaultCoreFactory(),
				new StandardEnvironment());
	}
	
	public static <T extends CtSimpleType<?>> T build(String packageName,
			String className) throws Exception {
		SpoonCompiler comp = new Launcher().createCompiler();
		comp.addInputSources(SpoonResourceHelper.resources("./src/test/java/"
				+ packageName.replace('.', '/') + "/" + className + ".java"));
		comp.build();
		return comp.getFactory().Package().get(packageName).getType(className);
	}

	public static <T extends CtSimpleType<?>> T build(String packageName,
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

}
