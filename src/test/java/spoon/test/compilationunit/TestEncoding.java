package spoon.test.compilationunit;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.cu.CompilationUnit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;

/**
 * Created by urli on 18/08/2017.
 */
public class TestEncoding {

    @Test
    public void testIsoEncodingIsSupported() throws Exception {

        File resource = new File("./src/test/resources/noclasspath/IsoEncoding.java");
        String content = new String(Files.readAllBytes(resource.toPath()), "ISO-8859-1");

        Launcher launcher = new Launcher();
        launcher.addInputResource(resource.getPath());
        launcher.getEnvironment().setNoClasspath(true);
        launcher.getEnvironment().setEncoding(Charset.forName("ISO-8859-1"));
        launcher.buildModel();

        CompilationUnit cu = launcher.getFactory().CompilationUnit().create(resource.getPath());
        assertEquals(content, cu.getOriginalSourceCode());
    }
}
