package maven-launcher.classpath-dependent-project.sorald.test;

import java.nio.file.Path;
import gumtree.spoon.diff.Diff;
import gumtree.spoon.AstComparator;

/**
 * App that computes the ratio of root operations to all opertions with gumtree-spoon!
 */
public class App {
    public static void main(String[] args) throws Exception {
        Path leftPath = Path.of(args[0]);
        Path rightPath = Path.of(args[1]);
        Diff diff = new AstComparator().compare(leftPath.toFile(), rightPath.toFile());
        double ratio = diff.getRootOperations().size() / diff.getAllOperations().size(); // Noncompliant; rule 2184 (cast arithmetic operator)
        System.out.println(ratio);
    }
}
