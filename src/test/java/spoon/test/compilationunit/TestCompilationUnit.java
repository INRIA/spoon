package spoon.test.compilationunit;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.test.api.testclasses.Bar;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * Created by urli on 18/08/2017.
 */
public class TestCompilationUnit {

    @Test
    public void testIsoEncodingIsSupported() throws Exception {

        File resource = new File("./src/test/resources/noclasspath/IsoEncoding.java");
        String content = new String(Files.readAllBytes(resource.toPath()), "ISO-8859-1");

        Launcher launcher = new Launcher();
        launcher.addInputResource(resource.getPath());
        launcher.getEnvironment().setNoClasspath(true);
        launcher.getEnvironment().setEncoding(Charset.forName("ISO-8859-1"));
        launcher.buildModel();

        CompilationUnit cu = launcher.getFactory().CompilationUnit().getOrCreate(resource.getPath());
        assertEquals(content, cu.getOriginalSourceCode());
    }

    @Test
    public void testGetUnitTypeWorksWithDeclaredType() {
        final Launcher launcher = new Launcher();
        launcher.addInputResource("./src/test/java/spoon/test/api/testclasses/Bar.java");
        launcher.buildModel();

        CtType type = launcher.getFactory().Type().get(Bar.class);
        CompilationUnit compilationUnit = type.getPosition().getCompilationUnit();

        assertEquals(CompilationUnit.UNIT_TYPE.TYPE_DECLARATION, compilationUnit.getUnitType());
    }

    @Test
    public void testGetUnitTypeWorksWithDeclaredPackage() {
        final Launcher launcher = new Launcher();
        launcher.addInputResource("./src/test/java/spoon/test/pkg/package-info.java");
        launcher.buildModel();

        CtPackage ctPackage = launcher.getFactory().Package().get("spoon.test.pkg");
        CompilationUnit compilationUnit = ctPackage.getPosition().getCompilationUnit();
        assertEquals(CompilationUnit.UNIT_TYPE.PACKAGE_DECLARATION, compilationUnit.getUnitType());
    }

    @Test
    public void testGetUnitTypeWorksWithCreatedObjects() {
        final Launcher launcher = new Launcher();
        CtPackage myPackage = launcher.getFactory().Package().getOrCreate("my.package");
        CompilationUnit cu = launcher.getFactory().createCompilationUnit();
        assertEquals(CompilationUnit.UNIT_TYPE.UNKNOWN, cu.getUnitType());

        cu.setDeclaredPackage(myPackage);
        assertEquals(CompilationUnit.UNIT_TYPE.PACKAGE_DECLARATION, cu.getUnitType());

        cu.setDeclaredTypes(Collections.singletonList(launcher.getFactory().createClass()));
        assertEquals(CompilationUnit.UNIT_TYPE.TYPE_DECLARATION, cu.getUnitType());
    }
}
