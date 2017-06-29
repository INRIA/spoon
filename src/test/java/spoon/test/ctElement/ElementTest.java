package spoon.test.ctElement;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtElement;
import spoon.support.reflect.declaration.CtAnnotationImpl;
import spoon.support.reflect.declaration.CtMethodImpl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

/**
 * Created by urli on 28/06/2017.
 */
public class ElementTest {

    @Test
    public void testGetFactory() {
        // contract: getFactory should always return an object
        // even if an element is created via its constructor
        // and not through the factory

        Launcher spoon = new Launcher();

        CtElement element = spoon.getFactory().createAnnotation();
        assertNotNull(element.getFactory());

        CtElement otherElement = new CtAnnotationImpl<>();
        assertNotNull(otherElement.getFactory());

        CtElement yetAnotherOne = new CtMethodImpl<>();
        assertNotNull(yetAnotherOne.getFactory());

        // contract: a singleton is used for the default factory
        assertSame(otherElement.getFactory(), yetAnotherOne.getFactory());
    }
}
