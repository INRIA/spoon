package fr.inria.spoon.dataflow;

import fr.inria.spoon.dataflow.scanners.CheckersScanner;
import fr.inria.spoon.dataflow.warning.Warning;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import spoon.Launcher;
import spoon.reflect.CtModel;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.fail;


public class TestRunner
{

    private List<String> getCommentWarnings(String line)
    {
        List<String> commentWarnings = new ArrayList<>();
        Matcher m = Pattern.compile("//@\\S*").matcher(line);
        while (m.find())
        {
            commentWarnings.add(m.group().substring(3));
        }
        return commentWarnings;
    }


    public void test(File file) throws IOException
    {
        Launcher launcher = new Launcher();
        launcher.getEnvironment().setNoClasspath(false);
        launcher.getEnvironment().setCommentEnabled(false);
        launcher.getEnvironment().setComplianceLevel(10);
        launcher.addInputResource(file.getAbsolutePath());
        CtModel model = launcher.buildModel();

        List<String> lines = Files.readAllLines(file.toPath(), Charset.defaultCharset());

        CheckersScanner scanner = new CheckersScanner(launcher.getFactory());
        model.getAllTypes().forEach(scanner::scan);
        List<Warning> warnings = scanner.getWarnings();

        for (Warning warn : warnings) {
            if (!lines.get(warn.position.getLine() - 1).contains("//@" + warn.kind.name())) {
                fail(String.format("ADDITIONAL WARNING '%s' AT (%s:%d)", warn.kind.name(),
                        warn.position.getFile().getAbsolutePath(), warn.position.getLine()));
            }
        }

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            List<String> commentWarnings = getCommentWarnings(line);
            int lineNumber = i + 1;
            for (String commentWarning : commentWarnings) {
                boolean isMissing =
                        warnings.stream().noneMatch(w -> w.position.getLine() == lineNumber
                                && w.kind.name().equals(commentWarning));
                if (isMissing) {
                    fail(String.format("MISSING WARNING '%s' AT (%s:%d)", commentWarning,
                            file.getAbsolutePath(), lineNumber));
                }
            }
        }
    }

    @TestFactory
    public List<DynamicTest> files()
    {
        List<DynamicTest> tests = new ArrayList<>();
        File testsDir = new File("src/test/java/fr/inria/spoon/dataflow/testcases");
        File[] files = testsDir.listFiles();
        for (File file : files)
        {
            tests.add(DynamicTest.dynamicTest(testsDir.getName(), () -> test(file)));
        }
        return tests;
    }
}
