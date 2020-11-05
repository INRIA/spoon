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
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.filter.TypeFilter;

// TODO: more doc, easier usage
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
	public static ArchitectureTest<CtExecutableReference<?>, CtModel> forbiddenInvocationCheck(
			Map<String, List<String>> methodsByType, IError<CtExecutableReference<?>> errorReporter) {
		// how handle System.out::println?
		Precondition<CtExecutableReference<?>> pre =
				Precondition.of(new TypeFilter<CtExecutableReference<?>>(CtExecutableReference.class));
		Constraint<CtExecutableReference<?>> con = Constraint.of(errorReporter, (element) -> {
			if (element != null && element.getDeclaringType() != null) {
				if (methodsByType
						.containsKey(element.getDeclaringType().getTopLevelType().getQualifiedName())) {
					List<String> calls = methodsByType.get(element.getDeclaringType().getQualifiedName());
					// we explicit use == because equals would allow any list with the "any_match" value.
					if (calls.contains(element.getSimpleName()) || calls == Wildcards.ANY_MATCH.getWildcard()) {
						return false;
					}
				}
			}
			return true;
		});
		return ArchitectureTest.of(pre, con);
	}

	/**
	 * This defines wildcards used in the matching process for easier usage.
	 */
	public enum Wildcards {
		/**
		 * Use this a value in the map, to forbid a whole class. E.g. key = java.io.PrintStream and value = ANY_MATCH.getWildcard() to forbid all system.out.print... calls.
		 */
		ANY_MATCH() {
			private List<String> anyMatch = Collections.unmodifiableList(Arrays.asList("<any_match>"));

			@Override
			public List<String> getWildcard() {
				return anyMatch;
			}
		};

		public abstract List<String> getWildcard();
	}
}
