package spoon.testing.utils;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.CtScanner;

import java.lang.reflect.Executable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

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
		return type == Launcher.class || type == CtModel.class || type == Factory.class;
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
		}

		throw new AssertionError("supportsParameter is not exhaustive");
	}

	private Launcher createLauncher(Executable method) {
		ModelTest annotation = method.getAnnotation(ModelTest.class);

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
		launcher.buildModel();

		// ensure that the model is valid
		launcher.getModel().getAllModules().forEach(ctModule -> {
			var invalidElements = ParentChecker.checkConsistency(ctModule);

			if (!invalidElements.isEmpty()) {
				throw new IllegalStateException("Model is inconsistent for %s, %d elements have invalid parents:%n%s".formatted(
					method.getName(),
					invalidElements.size(),
					invalidElements.stream()
						.map(ParentChecker.InvalidElement::toString)
						.limit(5)
						.collect(Collectors.joining(System.lineSeparator()))
				));
			}
		});

		return launcher;
	}

	private static final class ParentChecker extends CtScanner {
		private final List<InvalidElement> invalidElements;
		private final Deque<CtElement> stack;

		private ParentChecker() {
			this.invalidElements = new ArrayList<>();
			this.stack = new ArrayDeque<>();
		}

		public static List<InvalidElement> checkConsistency(CtElement ctElement) {
			ParentChecker parentChecker = new ParentChecker();
			parentChecker.scan(ctElement);
			return parentChecker.invalidElements;
		}

		@Override
		public void enter(CtElement element) {
			if (!this.stack.isEmpty() && (!element.isParentInitialized() || element.getParent() != this.stack.peek())) {
				this.invalidElements.add(new InvalidElement(element, this.stack));
			}

			this.stack.push(element);
		}

		@Override
		protected void exit(CtElement e) {
			this.stack.pop();
		}

		public record InvalidElement(CtElement element, Deque<CtElement> stack) {
			public InvalidElement {
				stack = new ArrayDeque<>(stack);
			}

			public String reason() {
				String name = this.element instanceof CtNamedElement ctNamedElement ? "-" + ctNamedElement.getSimpleName() : "";
				return (this.element.isParentInitialized() ? "inconsistent" : "null")
					+ " parent for " + this.element.getClass() + name
					+ " - " + this.element.getPosition()
					+ " - " + this.stack.peek();
			}

			public String dumpStack() {
				List<String> output = new ArrayList<>();

				for (CtElement ctElement : this.stack) {
					output.add("    " + ctElement.getClass().getSimpleName()
						+ " " + (ctElement.getPosition().isValidPosition() ? String.valueOf(ctElement.getPosition()) : "(?)")
					);
				}

				return String.join(System.lineSeparator(), output);
			}

			@Override
			public String toString() {
				return "%s%n%s".formatted(this.reason(), this.dumpStack());
			}

			@Override
			public boolean equals(Object object) {
				if (this == object) {
					return true;
				}
				if (!(object instanceof InvalidElement that)) {
					return false;
				}

				return this.element == that.element();
			}

			@Override
			public int hashCode() {
				return System.identityHashCode(this.element);
			}
		}
	}
}
