package spoon.support.util.compilation;

import java.util.Map;

class InMemoryClassLoader extends ClassLoader {

	private final Map<String, byte[]> compiledClasses;

	public InMemoryClassLoader(Map<String, byte[]> compiledClasses) {
		// Allow class requests to bubble up
		super(InMemoryClassLoader.class.getClassLoader());

		this.compiledClasses = compiledClasses;
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		if (compiledClasses.containsKey(name)) {
			byte[] bytes = compiledClasses.get(name);
			return defineClass(name, bytes, 0, bytes.length);
		}
		return super.findClass(name);
	}
}
