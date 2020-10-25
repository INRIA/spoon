package spoon.architecture.defaultChecks;

import java.util.List;
import java.util.Map;
import spoon.architecture.ArchitectureTest;
import spoon.architecture.Constraint;
import spoon.architecture.Precondition;
import spoon.architecture.errorhandling.IError;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.filter.TypeFilter;

public class ForbiddenInvocation {

	private ForbiddenInvocation() {

	}
	public static ArchitectureTest<CtInvocation<?>> forbiddenInvocationCheck(
			Map<String, List<String>> methodsByType, IError<CtInvocation<?>> errorReporter) {
		// how handle System.out::println?
		Precondition<CtInvocation<?>> pre =
				Precondition.of(new TypeFilter<CtInvocation<?>>(CtInvocation.class));
		Constraint<CtInvocation<?>> con = Constraint.of(errorReporter, (element) -> {
			CtExecutableReference<?> exec = element.getExecutable();
			if (exec != null && exec.getDeclaringType() != null) {
				if (methodsByType.containsKey(exec.getDeclaringType().getQualifiedName())) {
					List<String> calls = methodsByType.get(exec.getDeclaringType().getQualifiedName());
					if (calls.contains(exec.getSimpleName())) {
						return true;
					}
				}
			}
			return false;
		});
		return ArchitectureTest.of(pre, con);
	}

}
