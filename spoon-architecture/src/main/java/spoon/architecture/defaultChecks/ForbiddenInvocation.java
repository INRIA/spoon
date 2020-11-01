package spoon.architecture.defaultChecks;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import spoon.architecture.ArchitectureTest;
import spoon.architecture.Constraint;
import spoon.architecture.Precondition;
import spoon.architecture.errorhandling.IError;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.filter.TypeFilter;

//TODO: more doc, easier usage
/**
 * This defines an architecture test checking for forbidden invocations.
 * An invocation is forbidden if the target class and method name is part of the given list.
 * Use the {@code Wildcards} for advanced matching and easier usage.
 */
public class ForbiddenInvocation {

	private ForbiddenInvocation() {

	}
	/**
	 * Creates an forbidden invocation architecture test. An invocation is forbidden if it is contained in methodsByType.
	 * Method arguments/overloading are not respected for the lookups.
	 * @param methodsByType  an non null map from qualified class name to a list of method names.
	 * @param errorReporter  called if there is any forbidden invocation
	 * @return  an architecture test, checking invocations.
	 */
	public static ArchitectureTest<CtInvocation<?>, CtModel> forbiddenInvocationCheck(
			Map<String, List<String>> methodsByType, IError<CtInvocation<?>> errorReporter) {
		// how handle System.out::println?
		Precondition<CtInvocation<?>> pre =
				Precondition.of(new TypeFilter<CtInvocation<?>>(CtInvocation.class));
		Constraint<CtInvocation<?>> con = Constraint.of(errorReporter, (element) -> {
			CtExecutableReference<?> exec = element.getExecutable();
			if (exec != null && exec.getDeclaringType() != null) {
				if (methodsByType.containsKey(exec.getDeclaringType().getQualifiedName())) {
					List<String> calls = methodsByType.get(exec.getDeclaringType().getQualifiedName());
					// we explicit use == because equals would allow any list with the "any_match" value.
					if (calls.contains(exec.getSimpleName()) || calls == Wildcards.ANY_MATCH.getWildcard()) {
						return true;
					}
				}
			}
			return false;
		});
		return ArchitectureTest.of(pre, con);
	}
	/**
	 * This defines wildcards used in the matching process for easier usage.
	 */
	enum Wildcards {
		/**
		 * Use this a value in the map, to forbid a whole class. E.g. key = java.io.PrintStream and value = ANY_MATCH.getWildcard() to forbid all system.out.print... calls.
		 */
		ANY_MATCH() {
			private List<String> anyMatch = Collections.unmodifiableList(Arrays.asList("any_match"));
			@Override
			public List<String> getWildcard() {
				return anyMatch;
			}
		};

		public abstract List<String> getWildcard();
	}
}
