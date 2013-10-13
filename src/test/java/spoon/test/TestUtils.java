package spoon.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import spoon.reflect.Factory;
import spoon.reflect.declaration.CtSimpleType;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.builder.SpoonCompiler;
import spoon.support.builder.SpoonFile;
import spoon.support.builder.support.FileSystemFile;

public class TestUtils {

	public static CtSimpleType build(String packageName, String className ) throws Exception {
		SpoonCompiler comp = new SpoonCompiler();
		List<SpoonFile> files = new ArrayList();
		SpoonFile file = new FileSystemFile(new File("./src/test/java/"+packageName.replace('.', '/')+"/"+className+".java"));
		files.add(file);
		Factory factory = new Factory(new DefaultCoreFactory(), new StandardEnvironment());
		comp.compileSrc(factory, files);
		return factory.Package().get(packageName).getType(className);				
	}
	
}
