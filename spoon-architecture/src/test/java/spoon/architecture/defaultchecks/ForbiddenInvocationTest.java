package spoon.architecture.defaultchecks;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import spoon.SpoonException;
import spoon.architecture.defaultChecks.ForbiddenInvocation;
import spoon.architecture.errorhandling.ExceptionError;
import spoon.architecture.helper.CountingErrorCollector;
import spoon.architecture.helper.Models;
import spoon.reflect.CtModel;
import spoon.reflect.reference.CtExecutableReference;

public class ForbiddenInvocationTest {

	private String archError = "<Unit Test> ";

	@Test
	public void testInvocationInConstructor() {
		// contract: method invocations in constructors are scanned
		CtModel model = Models.createModelFromString(
				"src/test/resources/spoon/architecture/defaultchecks/forbiddeninvocation/InConstructor.java");
		Map<String, List<String>> methodsByName = new HashMap<>();
		methodsByName.put("java.io.PrintStream", Arrays.asList("println"));
		SpoonException exception = assertThrows(SpoonException.class, () -> ForbiddenInvocation
				.forbiddenInvocationCheck(methodsByName, new ExceptionError<>(archError)).runCheck(model));
		assertThat(exception).hasMessageThat().startsWith(archError);
		assertThat(exception).hasMessageThat().contains("println");
	}

	@Test
	public void testInvocationInFields() {
		// contract: method invocations in fields are scanned
		CtModel model = Models.createModelFromString(
				"src/test/resources/spoon/architecture/defaultchecks/forbiddeninvocation/InField.java");
		Map<String, List<String>> methodsByName = new HashMap<>();
		methodsByName.put("java.io.PrintStream", Arrays.asList("println"));
		SpoonException exception = assertThrows(SpoonException.class, () -> ForbiddenInvocation
				.forbiddenInvocationCheck(methodsByName, new ExceptionError<>(archError)).runCheck(model));
		assertThat(exception).hasMessageThat().startsWith(archError);
		assertThat(exception).hasMessageThat().contains("println");
	}

	@Test
	public void testInvocationInMethod() {
		// contract: method invocations in methods are scanned
		CtModel model = Models.createModelFromString(
				"src/test/resources/spoon/architecture/defaultchecks/forbiddeninvocation/InMethod.java");
		Map<String, List<String>> methodsByName = new HashMap<>();
		methodsByName.put("java.io.PrintStream", Arrays.asList("println"));
		SpoonException exception = assertThrows(SpoonException.class, () -> ForbiddenInvocation
				.forbiddenInvocationCheck(methodsByName, new ExceptionError<>(archError)).runCheck(model));
		assertThat(exception).hasMessageThat().startsWith(archError);
		assertThat(exception).hasMessageThat().contains("println");
	}

	@Test
	public void testInvocationInExecutableReference() {
		// contract: method invocations in methods are scanned
		CtModel model = Models.createModelFromString(
				"src/test/resources/spoon/architecture/defaultchecks/forbiddeninvocation/InExecutableReference.java");
		Map<String, List<String>> methodsByName = new HashMap<>();
		methodsByName.put("java.io.PrintStream", Arrays.asList("println"));
		SpoonException exception = assertThrows(SpoonException.class, () -> ForbiddenInvocation
				.forbiddenInvocationCheck(methodsByName, new ExceptionError<>(archError)).runCheck(model));
		assertThat(exception).hasMessageThat().startsWith(archError);
		assertThat(exception).hasMessageThat().contains("println");
	}

	@Test
	public void testInvocationWildcard() {
		// contract: wildcard matching finds both invocations
		CtModel model = Models.createModelFromString(
				"src/test/resources/spoon/architecture/defaultchecks/forbiddeninvocation/");
		Map<String, List<String>> methodsByName = new HashMap<>();
		CountingErrorCollector<CtExecutableReference<?>> collector = new CountingErrorCollector<>();
		methodsByName.put("java.io.PrintStream", ForbiddenInvocation.Wildcards.ANY_MATCH.getWildcard());
		ForbiddenInvocation.forbiddenInvocationCheck(methodsByName, collector).runCheck(model);
		// we have 2 references to executables print and println
		assertThat(collector.getCounter()).isEqualTo(2);
	}

}
