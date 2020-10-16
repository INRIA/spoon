package fr.inria.gforge.spoon.architecture.runner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;

public class CheckerRunner {

	public void runChecks(String srcPath, String testPath) {
		ModelBuilder builder = new ModelBuilder(srcPath, testPath);
		builder.getTestModel().getElements(new TypeFilter<CtMethod<?>>(CtMethod.class)).stream()
				.filter(v -> v.hasAnnotation(Architecture.class))
				.map(v -> v.getReference().getActualMethod())
				.filter(Objects::nonNull)
				.forEach(clazz -> invokeMethod(builder, clazz));
	}

	private void invokeMethod(ModelBuilder builder, Method clazz) {
		try {
			clazz.invoke(createCallTarget(clazz), createArguments(builder));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| InstantiationException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	private Object createCallTarget(Method clazz) throws InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		return clazz.getDeclaringClass().getDeclaredConstructor().newInstance();
	}
	private Object[] createArguments(ModelBuilder builder) {
		return new Object[]{builder.getMainModel(), builder.getTestModel()};
	}
}
