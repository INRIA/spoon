package spoon.test.path;

import org.junit.BeforeClass;
import org.junit.Test;
import spoon.Launcher;
import spoon.MavenLauncher;
import spoon.reflect.CtModel;
import spoon.reflect.CtModelImpl;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.path.CtElementPathBuilder;
import spoon.reflect.path.CtPath;
import spoon.reflect.path.CtPathException;
import spoon.reflect.path.CtPathStringBuilder;
import spoon.reflect.visitor.filter.TypeFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class UniqueRolePathTest {
    static Launcher launcher;
    static CtPackage rootPackage;

    /**
     * load model once into static variable and use it for more read-only tests
     */
    @BeforeClass
    public static void loadModel() {
        // we have to remove the test-classes folder
        // so that the precondition of --source-classpath is not violated
        // (target/test-classes contains src/test/resources which itself contains Java files)
        StringBuilder classpath = new StringBuilder();
        for (String classpathEntry : System.getProperty("java.class.path").split(File.pathSeparator)) {
            if (!classpathEntry.contains("test-classes")) {
                classpath.append(classpathEntry);
                classpath.append(File.pathSeparator);
            }
        }
        String systemClassPath = classpath.substring(0, classpath.length() - 1);

        launcher = new Launcher();

        launcher.run(new String[] {
                "-i", "src/main/java",
                "-o", "target/spooned",
                "--destination","target/spooned-build",
                "--source-classpath", systemClassPath,
                "--compile", // compiling Spoon code itself on the fly
                "--compliance", "8",
                "--level", "OFF"
        });

        rootPackage = launcher.getFactory().Package().getRootPackage();
    }
    @Test
    public void testElementToPathToElementEquivalency() {//} throws CtPathException {
        System.out.println("coucou");;
        CtModel model = launcher.getModel();
        List l = new ArrayList();
        l.add(rootPackage);

        for(Object o : model.getElements(new TypeFilter(CtElement.class))) {
            CtElement element = (CtElement) o;
            if(element.getParent(CtModelImpl.CtRootPackage.class) == rootPackage) {
                try {
                CtPath path = new CtElementPathBuilder().fromElement(element, model.getRootPackage());
                System.out.println("Path: " + path);
                    Collection<CtElement> returnedElements = path.evaluateOn(l);
                    assertEquals(returnedElements.size(), 1);
                    CtElement actualElement = (CtElement) returnedElements.toArray()[0];
                    assertEquals(element, actualElement);
                } catch (Exception e) {
                    System.out.println("error");
                }
            }
        }
    }
}
