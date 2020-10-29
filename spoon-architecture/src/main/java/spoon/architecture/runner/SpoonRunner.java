package spoon.architecture.runner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import spoon.SpoonException;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;

public class SpoonRunner implements IRunner<CtModel> {

	private IModelBuilder<CtModel> builder;

	public SpoonRunner(IModelBuilder<CtModel> builder) {
		this.builder = builder;
	}
	@Override
	public Iterable<Method> selectMethods(CtModel model) {
		return model.getElements(new TypeFilter<CtMethod<?>>(CtMethod.class)).stream()
				.filter(v -> v.hasAnnotation(Architecture.class))
				.map(v -> v.getReference().getActualMethod())
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

	}

	@Override
	public void invokeMethod(Method method) {
		try {
			method.invoke(createCallTarget(method), createArguments(method));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| InstantiationException | NoSuchMethodException | SecurityException e) {
			if (e.getCause() instanceof SpoonException) {
				throw (SpoonException) e.getCause();
			}
			e.printStackTrace();
		}
	}

	private Object createCallTarget(Method method) throws InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		return method.getDeclaringClass().getDeclaredConstructor().newInstance();
	}
	private Object[] createArguments(Method method) {
		System.out.println("creating args for " + method.getName() + " count: " + method.getParameterCount());
		List<Object> modelParameter = new ArrayList<>();
		String[] modelNames = method.getAnnotation(Architecture.class).modelNames();
		for (String name : modelNames) {
			modelParameter.add(builder.getModelWithIdentifier(name.toLowerCase()));
		}
		return modelParameter.toArray();
	}
}
