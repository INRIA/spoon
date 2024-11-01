package spoon.support.util.compilation;

import java.util.HashMap;
import java.util.Map;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;

class ClassFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

	private final Map<String, InMemoryOutputObject> outputFileMap;

	public ClassFileManager(StandardJavaFileManager fileManager) {
		super(fileManager);

		this.outputFileMap = new HashMap<>();
	}

	@Override
	public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind,
		FileObject sibling) {
		InMemoryOutputObject outputObject = new InMemoryOutputObject(className);

		outputFileMap.put(className, outputObject);

		return outputObject;
	}

	Map<String, InMemoryOutputObject> getAll() {
		return outputFileMap;
	}
}
