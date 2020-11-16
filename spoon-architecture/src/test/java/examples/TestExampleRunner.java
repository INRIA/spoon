package examples;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import spoon.SpoonException;
import spoon.architecture.report.ShellPrinter;
import spoon.architecture.runner.Architecture;
import spoon.architecture.runner.IModelBuilder;
import spoon.architecture.runner.ISpoonArchitecturalChecker;
import spoon.architecture.runner.ModelBuilder;
import spoon.architecture.runner.SpoonArchitecturalChecker;
import spoon.architecture.runner.SpoonRunner;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;

public class TestExampleRunner {

	@Test
	public void spoonExampleMustRun() {
		List<Pair<String, String>> testObjects = new ArrayList<>();
		testObjects.add(Pair.of("testmodel", "src/test/java/examples/spoon"));

		testObjects.add(Pair.of("annotationTests", "src/test/resources/examples/spoon/annotations/"));
		testObjects.add(Pair.of("arrayTests", "src/test/resources/examples/spoon/arrays/"));
		testObjects.add(Pair.of("assignments", "src/test/resources/examples/spoon/assignments/"));
		testObjects.add(Pair.of("catchvariables", "src/test/resources/examples/spoon/catches/CatchVariables.java"));
		testObjects.add(Pair.of("parameters", "src/test/resources/examples/spoon/parameters/UnusedParameters.java"));
		testObjects.add(Pair.of("anonymousexec", "src/test/resources/examples/spoon/anonymousexec/Foo.java"));
		testObjects.add(Pair.of("breaks", "src/test/resources/examples/spoon/parameters/UnusedParameters.java"));


		// SpoonArchitecturalCheckerImpl.createChecker().runChecks();
		IModelBuilder<CtModel> builder = new ModelBuilder();
		testObjects.forEach(v -> builder.insertInputPath(v.getKey(), v.getValue()));
		ISpoonArchitecturalChecker checker = new SpoonArchitecturalChecker.Builder()
		.addRunner(new MustFailRunner(builder))
		.addModelBuilder(builder)
		.addReportPrinter(new ShellPrinter())
		.build();
		checker.runChecks("testmodel");
	}

	class MustFailRunner extends SpoonRunner {

		private IModelBuilder<CtModel> builder;

		MustFailRunner(IModelBuilder<CtModel> builder) {
			super(builder);
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
					return;
				}
				e.printStackTrace();
			}
			throw new AssertionError(String.format("All examples must fail. %s is not failing", method.getName()));
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
}
