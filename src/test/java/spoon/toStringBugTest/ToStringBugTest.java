package spoon.toStringBugTest;

import org.junit.Test;
import spoon.Launcher;
import spoon.SpoonException;
import spoon.reflect.declaration.CtType;

import java.util.List;

import static org.junit.Assert.fail;

public class ToStringBugTest {
    
	@Ignore("UnresolvedBug")
	@GitHubIssue(issueNumber = 3381)
    @Test
    public void test1() {
        final Launcher launcher = new Launcher();
        launcher.getEnvironment().setNoClasspath(true);
        // folder with both classes
        launcher.addInputResource("./src/test/resources/toStringBugTest");
        launcher.buildModel();
        List<CtType<?>> all = launcher.getFactory().Class().getAll();

        // first class of resources
        CtType<?> ctType = all.get(0);
        try {
            String s = ctType.toString();
        } catch (SpoonException e) {
            // spoon.SpoonException: Unexpected next line after last line
            e.printStackTrace();
            fail();
        }
    }
    
	@Ignore("UnresolvedBug")
	@GitHubIssue(issueNumber = 3381)
    @Test
    public void test2() {
        final Launcher launcher = new Launcher();
        launcher.getEnvironment().setNoClasspath(true);
        // folder with both classes
        launcher.addInputResource("./src/test/resources/toStringBugTest");
        launcher.buildModel();
        List<CtType<?>> all = launcher.getFactory().Class().getAll();

        // second class of resources
        CtType<?> ctType = all.get(1);
        try {
            String s = ctType.toString();
        } catch (SpoonException e) {
            // spoon.SpoonException: Unexpected next line after last line
            e.printStackTrace();
            fail();
        }
    }
}
