package spoon.test.imports.testclasses;

import java.lang.annotation.Annotation;
import java.nio.charset.Charset;

import org.junit.jupiter.api.Test;
import static java.nio.charset.Charset.forName;
import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Created by urli on 04/10/2017.
 */
public class StaticNoOrdered {

    public void testMachin() {
        assertEquals("bla","truc");
        assertEquals(7,12);
        assertEquals(new String[0],new String[0]);
        Annotation test = (Test) null;
    }

    public void anotherStaticImoport() {
        Charset charset = forName("utf-8");
    }
}
