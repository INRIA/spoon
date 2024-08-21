package spoon.support.util.compilation;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.file.Paths;

class InMemoryOutputObject extends SimpleJavaFileObject {

	private final ByteArrayOutputStream outputStream;
	private final String name;

	InMemoryOutputObject(String fullyQualifiedName) {
		// A path does necessarily exist
		super(Paths.get(fullyQualifiedName).toUri(), Kind.CLASS);

		this.name = fullyQualifiedName;
		this.outputStream = new ByteArrayOutputStream();
	}

	@Override
	public OutputStream openOutputStream() {
		return outputStream;
	}

	@Override
	public String getName() {
		return name;
	}

	byte[] getContent() {
		return outputStream.toByteArray();
	}
}
