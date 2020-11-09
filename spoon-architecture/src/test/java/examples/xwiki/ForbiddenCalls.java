package examples.xwiki;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spoon.architecture.defaultChecks.ForbiddenInvocation;
import spoon.architecture.errorhandling.IError;
import spoon.architecture.runner.Architecture;
import spoon.reflect.CtModel;
import spoon.reflect.reference.CtExecutableReference;

public class ForbiddenCalls {

	private Map<String, List<String>> methodsByType = new HashMap<>();
	// here would the type be added, input could be more complex like reading a file

	@Architecture
	public void forbiddenCallsTest(CtModel srcModel, CtModel testModel) {
		// blocks all invocations of System.out.print like println/print/printf etc.
		methodsByType.put("java.io.PrintStream", ForbiddenInvocation.Wildcards.ANY_MATCH.getWildcard());
		ForbiddenInvocation.forbiddenInvocationCheck(methodsByType, new ErrorReporter()).runCheck(srcModel);
		ForbiddenInvocation.forbiddenInvocationCheck(methodsByType, new ErrorReporter()).runCheck(testModel);
	}

	private class ErrorReporter implements IError<CtExecutableReference<?>> {

		/**
		 * Error reporting of any kind is possible here.
		 */
		@Override
		public void printError(CtExecutableReference<?> element) {
			System.out.println(String.format("There's an invocation of a forbidden method [%s]", element));
		}

	}
}
