package spoon.test;

import spoon.Spoon;
import spoon.compiler.SpoonCompiler;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.Factory;
import spoon.reflect.declaration.CtSimpleType;

public class TestUtils {

	public static <T extends CtSimpleType<?>> T build(String packageName,
			String className) throws Exception {
		SpoonCompiler comp = Spoon.createCompiler();
		comp.addInputSources(SpoonResourceHelper.resources("./src/test/java/"
				+ packageName.replace('.', '/') + "/" + className + ".java"));
		comp.build();
		return comp.getFactory().Package().get(packageName).getType(className);
	}

	public static Factory build(Class<?>... classesToBuild) throws Exception {
		SpoonCompiler comp = Spoon.createCompiler();
        for (Class classToBuild : classesToBuild) {
		comp.addInputSources(SpoonResourceHelper.resources("./src/test/java/"
				+ classToBuild.getName().replace('.', '/') + ".java"));
        }
		comp.build();
		return comp.getFactory();
	}

}
