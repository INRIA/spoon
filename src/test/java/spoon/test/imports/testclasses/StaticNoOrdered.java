package spoon.test.imports.testclasses;


import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static java.nio.charset.Charset.forName;
import java.lang.annotation.Annotation;
import java.nio.charset.Charset;


/**
 * Created by urli on 04/10/2017.
 */
public class StaticNoOrdered {

    public void testMachin() {
        assertEquals("bla","truc");
        assertEquals(7,12);
        assertEquals(new String[0],new String[0]);
        Test test = new Test() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public Class<? extends Throwable> expected() {
                return null;
            }

            @Override
            public long timeout() {
                return 0;
            }
        };
    }

    public void anotherStaticImoport() {
        Charset charset = forName("utf-8");
    }
}
