package spoon.test.ctIf;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.CtIf;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

/**
 * Created by urli on 20/04/2017.
 */
public class CtIfTest {

    @Test
    public void testHashCodeOfCtIf() {
        // contract: two different CtIf should have different hashcodes

        Launcher spoon = new Launcher();
        spoon.addInputResource("./src/test/java/spoon/test/ctIf/testclasses/ASampleClass.java");
        spoon.buildModel();

        CtClass<?> sampleClass = spoon.getFactory().getModel().getElements(new TypeFilter<CtClass>(CtClass.class)).get(0);
        CtMethod<?> staticMethod = sampleClass.getMethodsByName("aStaticMethod").get(0);

        List<CtIf> ctIfs = staticMethod.getBody().filterChildren(new TypeFilter<CtIf>(CtIf.class)).list();

        assertThat(ctIfs.size(), is(2));

        CtIf firstIf = ctIfs.get(0);
        CtIf secondIf = ctIfs.get(1);

        assertThat(firstIf, not(secondIf));
        assertThat(System.identityHashCode(firstIf), not(System.identityHashCode(secondIf)));
        assertThat(firstIf.hashCode(), not(secondIf.hashCode()));
    }
}
