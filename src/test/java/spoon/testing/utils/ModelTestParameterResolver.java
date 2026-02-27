package spoon.testing.utils;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.ModelConsistencyCheckerTestHelper;
import spoon.support.compiler.VirtualFile;

import java.lang.reflect.Executable;

public class ModelTestParameterResolver implements ParameterResolver {

	private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create("spoon", "modeltest");

	@Override
	public boolean supportsParameter(
		ParameterContext parameterContext,
		ExtensionContext extensionContext
	) throws ParameterResolutionException {
		if (!parameterContext.getDeclaringExecutable().isAnnotationPresent(ModelTest.class)) {
			return false;
		}
		Class<?> type = parameterContext.getParameter().getType();
		return type == Launcher.class || type == CtModel.class || type == Factory.class
			|| CtType.class.isAssignableFrom(type);
	}

	@Override
	public Object resolveParameter(
		ParameterContext parameterContext,
		ExtensionContext extensionContext
	) throws ParameterResolutionException {
		Executable method = parameterContext.getDeclaringExecutable();

		Launcher launcher = (Launcher) extensionContext.getStore(NAMESPACE)
			.getOrComputeIfAbsent(method, this::createLauncher);

		if (parameterContext.getParameter().getType() == Launcher.class) {
			return launcher;
		} else if (parameterContext.getParameter().getType() == CtModel.class) {
			return launcher.getModel();
		} else if (parameterContext.getParameter().getType() == Factory.class) {
			return launcher.getFactory();
		} else if (parameterContext.isAnnotated(BySimpleName.class)
			&& CtType.class.isAssignableFrom(parameterContext.getParameter().getType())) {
			String name = parameterContext.findAnnotation(BySimpleName.class)
				.map(BySimpleName::value)
				.orElseThrow();
			return launcher.getModel().getAllTypes().stream()
				.filter(type -> type.getSimpleName().equals(name))
				.findFirst()
				.orElseThrow(() -> new ParameterResolutionException("no type with simple name " + name + " found"));
		} else if (parameterContext.isAnnotated(ByClass.class)
			&& CtType.class.isAssignableFrom(parameterContext.getParameter().getType())) {
			Class<?> clazz = parameterContext.findAnnotation(ByClass.class)
				.map(ByClass::value)
				.orElseThrow();
			CtClass<?> ctClass = launcher.getFactory().Class().get(clazz.getName());
			if (ctClass == null) {
				throw new ParameterResolutionException("no type with name " + clazz.getName() + " found");
			}
			return ctClass;
		}

		throw new ParameterResolutionException("supportsParameter is not exhaustive (" + parameterContext + ")");
	}

	private Launcher createLauncher(Executable method) {
		ModelTest annotation = method.getAnnotation(ModelTest.class);

		if (annotation.value().length == 0 && annotation.code().length == 0) {
			throw new IllegalArgumentException(
				"""
					@ModelTest on %s must specify either 'value' (file paths) or 'code' (inline source), or both.
					Example: @ModelTest(code = "class Foo {}") or @ModelTest(value = "src/test/resources/...")"""
					.formatted(method.getName())
			);
		}

		Launcher launcher = new Launcher();
		if (annotation.complianceLevel() > 0) {
			launcher.getEnvironment().setComplianceLevel(annotation.complianceLevel());
		}
		launcher.getEnvironment().setCommentEnabled(annotation.commentsEnabled());
		launcher.getEnvironment().setAutoImports(annotation.autoImport());
		launcher.getEnvironment().setNoClasspath(annotation.noClasspath());
		for (String path : annotation.value()) {
			launcher.addInputResource(path);
		}
		// we use a indexed for loop here to have distinct names for each file
		for (int i = 0; i < annotation.code().length; i++) {
			String virtualFileName = method.getName() + "_code_" + i++ + ".java";
			launcher.addInputResource(new VirtualFile(annotation.code()[i], virtualFileName));
		}
		launcher.buildModel();

		ModelConsistencyCheckerTestHelper.assertModelIsConsistent(launcher.getFactory());

		return launcher;
	}
}
