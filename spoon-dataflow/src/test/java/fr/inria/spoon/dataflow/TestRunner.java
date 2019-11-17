package fr.inria.spoon.dataflow;

import fr.inria.spoon.dataflow.scanners.CheckersScanner;
import fr.inria.spoon.dataflow.warning.Warning;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import spoon.Launcher;
import spoon.reflect.CtModel;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class TestRunner
{
    private File file;

    private String name;

    public TestRunner(File file, String name)
    {
        this.file = file;
        this.name = name;
    }

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

    @Test
    public void test() throws IOException
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

        for (Warning warn : warnings)
        {
            if (!lines.get(warn.position.getLine() - 1).contains("//@" + warn.kind.name()))
            {
                fail(String.format("ADDITIONAL WARNING '%s' AT (%s:%d)",
                    warn.kind.name(), warn.position.getFile().getAbsolutePath(), warn.position.getLine()));
            }
        }

        for (int i = 0; i < lines.size(); i++)
        {
            String line = lines.get(i);
            List<String> commentWarnings = getCommentWarnings(line);
            int lineNumber = i + 1;
            for (String commentWarning : commentWarnings)
            {
                boolean isMissing = warnings.stream()
                                            .noneMatch(w -> w.position.getLine() == lineNumber
                                                            && w.kind.name().equals(commentWarning));
                if (isMissing)
                {
                    fail(String.format("MISSING WARNING '%s' AT (%s:%d)",
                        commentWarning, file.getAbsolutePath(), lineNumber));
                }
            }
        }
    }

    @Parameterized.Parameters(name="{1}")
    public static Collection<Object[]> files()
    {
        File testsDir = new File("src/test/java/fr/inria/spoon/dataflow/testcases");
        File[] files = testsDir.listFiles();
        List<Object[]> parameters = new ArrayList<>();
        for (File file : files)
        {
            parameters.add(new Object[] { file, file.getName() });
        }
        return parameters;
    }
}
