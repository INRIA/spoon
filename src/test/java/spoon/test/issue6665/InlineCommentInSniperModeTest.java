package spoon.test.issue6665;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtComment;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.PrettyPrinter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.sniper.SniperJavaPrettyPrinter;
import spoon.testing.utils.GitHubIssue;
import spoon.testing.utils.LineSeparatorExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InlineCommentInSniperModeTest {

	@Test
	@ExtendWith(LineSeparatorExtension.class)
	@GitHubIssue(issueNumber = 6665, fixed = false)
	void testSniperModeInlineComment() {
		// contract: inline comments are printed with a newline at the end in the sniper mode

		final Launcher launcher = new Launcher();
		launcher.addInputResource("src/test/java/spoon/test/issue6665/testclasses/SampleClassIssue6665.java");
		Environment environment = launcher.getEnvironment();
		environment.setPrettyPrinterCreator(() -> new SniperJavaPrettyPrinter(environment));
		PrettyPrinter printer = environment.createPrettyPrinter();
		launcher.buildModel();

		CtModel model = launcher.getModel();
		CtMethod<?> main = model.getElements(new TypeFilter<>(CtMethod.class)).get(0);
		Factory factory = launcher.getFactory();
		CtComment comment = factory.createInlineComment("TEST 1");
		main.setComments(List.of(comment));

		CtCompilationUnit cu = factory.CompilationUnit().getMap().values().iterator().next();
		assertEquals("""
			package spoon.test.issue6665.testclasses;
			public class SampleClassIssue6665 {
				// TEST 1
				public static void main(String[] args) {
				}
			}
			""", printer.printCompilationUnit(cu));

	}
}
