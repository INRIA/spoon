package spoon.support.util.compilation;

import javax.tools.SimpleJavaFileObject;
import java.nio.file.Paths;

class InMemoryInputObject extends SimpleJavaFileObject {

	private final String name;
	private final String content;

	public InMemoryInputObject(String fullyQualifiedName, String content) {
		super(Paths.get(fullyQualifiedName).toUri(), Kind.SOURCE);
		this.name = fullyQualifiedName;

		this.content = content;
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		return content;
	}

	@Override
	public String getName() {
		return name;
	}
}
