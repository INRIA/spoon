package spoon.javadoc.api;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import spoon.Launcher;
import spoon.javadoc.api.elements.snippets.JavadocSnippetMarkupRegion;
import spoon.javadoc.api.elements.snippets.JavadocSnippetRegionType;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;

public class TestHelper {

	public static CtType<?> parseType(Class<?> clazz) {
		Launcher launcher = new Launcher();
		launcher.getEnvironment().setCommentEnabled(true);
		launcher.getEnvironment().setComplianceLevel(11);
		launcher.addInputResource("src/test/java/" + clazz.getName().replace(".", "/") + ".java");
		return launcher.buildModel().getAllTypes().iterator().next();
	}

	public static Object invokeMethod(CtMethod<?> method) {
		if (!method.isStatic()) {
			throw new IllegalArgumentException("Spoon method must be static");
		}
		Class<?> actualClass = method.getDeclaringType().getActualClass();
		Method actualMethod = Arrays.stream(actualClass.getDeclaredMethods())
			.filter(it -> it.getName().equals(method.getSimpleName()))
			.findAny()
			.orElseThrow();

		try {
			List<Object> parameters = new ArrayList<>();
			for (Class<?> parameterType : actualMethod.getParameterTypes()) {
				if (parameterType == Factory.class) {
					parameters.add(method.getFactory());
				} else {
					parameters.add((Object) MethodHandles.zero(parameterType).invoke());
				}
			}

			actualMethod.setAccessible(true);
			return actualMethod.invoke(null, parameters.toArray());
		} catch (Throwable e) {
			throw new RuntimeException("Failed to invoke method", e);
		}
	}

	public static JavadocSnippetMarkupRegion region(
		int startLine, int endLine, Map<String, String> attributes, JavadocSnippetRegionType type
	) {
		return new JavadocSnippetMarkupRegion(startLine, endLine, attributes, type);
	}
}
