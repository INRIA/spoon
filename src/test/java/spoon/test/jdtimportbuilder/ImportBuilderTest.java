package spoon.test.jdtimportbuilder;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.util.SortedList;
import spoon.test.imports.testclasses.A;
import spoon.test.imports.testclasses.ClassWithInvocation;
import spoon.test.jdtimportbuilder.testclasses.StarredImport;
import spoon.test.jdtimportbuilder.testclasses.StaticImport;
import spoon.test.jdtimportbuilder.testclasses.StaticImportWithInheritance;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by urli on 09/08/2017.
 */
public class ImportBuilderTest {

    @Test
    public void testWithNoImport() {
        // contract: when the source code has no import, none is created when building model
        Launcher spoon = new Launcher();
        spoon.addInputResource("./src/test/java/spoon/test/imports/testclasses/A.java");
        spoon.getEnvironment().setAutoImports(true);
        spoon.buildModel();

        CtClass classA = spoon.getFactory().Class().get(A.class);
        CompilationUnit unitA = spoon.getFactory().CompilationUnit().getMap().get(classA.getPosition().getFile().getPath());
        assertTrue(unitA.getImports().isEmpty());
    }

    @Test
    public void testWithSimpleImport() {
        // contract: when the source has one import, the same import is created as a reference in auto-import mode
        Launcher spoon = new Launcher();
        spoon.addInputResource("./src/test/java/spoon/test/imports/testclasses/ClassWithInvocation.java");
        spoon.getEnvironment().setAutoImports(true);
        spoon.buildModel();

        CtClass classA = spoon.getFactory().Class().get(ClassWithInvocation.class);
        CompilationUnit unitA = spoon.getFactory().CompilationUnit().getMap().get(classA.getPosition().getFile().getPath());
        Collection<CtReference> imports = unitA.getImports();

        assertEquals(1, imports.size());

        CtReference ref = imports.iterator().next();
        assertEquals("GlobalAnnotation", ref.getSimpleName());
        assertTrue(ref instanceof CtTypeReference);

        CtTypeReference refType = (CtTypeReference)ref;
        assertEquals("spoon.test.annotation.testclasses.GlobalAnnotation", refType.getQualifiedName());
    }

    @Test
    public void testWithSimpleImportNoAutoimport() {
        // contract: when the source has one import, nothing is imported when not in autoimport
        Launcher spoon = new Launcher();
        spoon.addInputResource("./src/test/java/spoon/test/imports/testclasses/ClassWithInvocation.java");
        spoon.getEnvironment().setAutoImports(false);
        spoon.buildModel();

        CtClass classA = spoon.getFactory().Class().get(ClassWithInvocation.class);
        CompilationUnit unitA = spoon.getFactory().CompilationUnit().getMap().get(classA.getPosition().getFile().getPath());
        assertTrue(unitA.getImports().isEmpty());
    }

    @Test
    public void testInternalImportWhenNoClasspath() {
        // contract: in no-classpath anything which is not loaded cannot be imported, even if original source code has imports
        Launcher spoon = new Launcher();
        spoon.addInputResource("./src/test/resources/noclasspath/Attachment.java");
        spoon.getEnvironment().setAutoImports(true);
        spoon.getEnvironment().setNoClasspath(true);
        spoon.buildModel();

        CtClass classA = spoon.getFactory().Class().get("it.feio.android.omninotes.models.Attachment");
        CompilationUnit unitA = spoon.getFactory().CompilationUnit().getMap().get(classA.getPosition().getFile().getPath());
        assertTrue(unitA.getImports().isEmpty());
    }

    @Test
    public void testSimpleStaticImport() {
        // contract: simple static import are imported correctly
        Launcher spoon = new Launcher();
        spoon.addInputResource("./src/test/java/spoon/test/jdtimportbuilder/testclasses/StaticImport.java");
        spoon.getEnvironment().setAutoImports(true);
        spoon.buildModel();

        CtClass classA = spoon.getFactory().Class().get(StaticImport.class);
        CompilationUnit unitA = spoon.getFactory().CompilationUnit().getMap().get(classA.getPosition().getFile().getPath());
        Collection<CtReference> imports = unitA.getImports();

        assertEquals(1, imports.size());

        CtReference ref = imports.iterator().next();

        assertTrue(ref instanceof CtFieldReference);
        assertEquals("spoon.test.jdtimportbuilder.testclasses.staticimport.Dependency#ANY", ((CtFieldReference) ref).getQualifiedName());
    }

    @Test
    public void testWithStaticStarredImportFromInterface() {
        // contract: when a starred import is used with a target package, all classes of the package should be imported
        Launcher spoon = new Launcher();
        spoon.addInputResource("./src/test/java/spoon/test/jdtimportbuilder/testclasses/StarredImport.java");
        spoon.addInputResource("./src/test/java/spoon/test/jdtimportbuilder/testclasses/fullpack/");
        spoon.getEnvironment().setAutoImports(true);
        spoon.buildModel();

        CtClass classA = spoon.getFactory().Class().get(StarredImport.class);
        CompilationUnit unitA = spoon.getFactory().CompilationUnit().getMap().get(classA.getPosition().getFile().getPath());
        Collection<CtReference> imports = unitA.getImports();

        assertEquals(3, imports.size());

        Iterator<CtReference> iterator = imports.iterator();
        CtReference firstRef = iterator.next();
        CtReference secondRef = iterator.next();
        CtReference thirdRef = iterator.next();

        assertTrue(firstRef instanceof CtTypeReference);
        assertTrue(secondRef instanceof CtTypeReference);
        assertTrue(thirdRef instanceof CtTypeReference);

        List<String> importNames = new SortedList<String>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

        importNames.add(((CtTypeReference) firstRef).getQualifiedName());
        importNames.add(((CtTypeReference) secondRef).getQualifiedName());
        importNames.add(((CtTypeReference) thirdRef).getQualifiedName());

        assertEquals("spoon.test.jdtimportbuilder.testclasses.fullpack.A", importNames.get(0));
        assertEquals("spoon.test.jdtimportbuilder.testclasses.fullpack.B", importNames.get(1));
        assertEquals("spoon.test.jdtimportbuilder.testclasses.fullpack.C", importNames.get(2));
    }

    @Test
    public void testWithStaticInheritedImport() {
        // contract: static field or methods can be inherited, JDTImportBuilder must retrieve the imported type from the right class
        Launcher spoon = new Launcher();
        spoon.addInputResource("./src/test/java/spoon/test/jdtimportbuilder/testclasses/StaticImportWithInheritance.java");
        spoon.getEnvironment().setAutoImports(true);
        spoon.getEnvironment().setShouldCompile(true);
        spoon.setSourceOutputDirectory("./target/spoon-jdtimport-inheritedstatic");
        spoon.run();

        CtClass classStatic = spoon.getFactory().Class().get(StaticImportWithInheritance.class);
        CompilationUnit unitStatic = spoon.getFactory().CompilationUnit().getMap().get(classStatic.getPosition().getFile().getPath());
        Collection<CtReference> imports = unitStatic.getImports();

        assertEquals(4, imports.size());

        List<String> importNames = new SortedList<String>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

        for (CtReference refImport : imports) {
            if (refImport instanceof CtFieldReference) {
                importNames.add(((CtFieldReference) refImport).getQualifiedName());
            } else if (refImport instanceof CtExecutableReference) {
                importNames.add(((CtExecutableReference) refImport).getDeclaringType().getQualifiedName() + CtMethod.EXECUTABLE_SEPARATOR + refImport.getSimpleName());
            }

        }

        assertEquals("spoon.test.jdtimportbuilder.testclasses.staticimport.Dependency#ANY", importNames.get(0));
        assertEquals("spoon.test.jdtimportbuilder.testclasses.staticimport.Dependency#TRUE", importNames.get(1));
        assertEquals("spoon.test.jdtimportbuilder.testclasses.staticimport.Dependency#maMethod", importNames.get(2));
        assertEquals("spoon.test.jdtimportbuilder.testclasses.staticimport.DependencySubClass#OTHER_INT", importNames.get(3));
    }

    @Test
    public void testWithImportFromItf() {
        // contract: When using static import of an interface, only static method and fields should be imported
        Launcher spoon = new Launcher();
        spoon.addInputResource("./src/test/resources/jdtimportbuilder/");
        spoon.getEnvironment().setAutoImports(true);
        spoon.getEnvironment().setShouldCompile(true);
        spoon.setSourceOutputDirectory("./target/spoon-jdtimport-itfimport");
        spoon.run();

        CtClass classStatic = spoon.getFactory().Class().get("jdtimportbuilder.ItfImport");
        CompilationUnit unitStatic = spoon.getFactory().CompilationUnit().getMap().get(classStatic.getPosition().getFile().getPath());
        Collection<CtReference> imports = unitStatic.getImports();

        assertEquals(2, imports.size());

        List<String> importNames = new SortedList<String>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

        for (CtReference refImport : imports) {
            if (refImport instanceof CtFieldReference) {
                importNames.add(((CtFieldReference) refImport).getQualifiedName());
            } else if (refImport instanceof CtExecutableReference) {
                importNames.add(((CtExecutableReference) refImport).getDeclaringType().getQualifiedName() + CtMethod.EXECUTABLE_SEPARATOR + refImport.getSimpleName());
            }
        }

        assertEquals("jdtimportbuilder.itf.DumbItf#MYSTRING", importNames.get(0));
        assertEquals("jdtimportbuilder.itf.DumbItf#anotherStaticMethod", importNames.get(1));
    }

}
