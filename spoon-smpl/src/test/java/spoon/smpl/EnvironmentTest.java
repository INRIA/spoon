package spoon.smpl;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import static spoon.smpl.TestUtils.*;

public class EnvironmentTest {
    @Test
    public void testJoinPositives() {
        Environment e1 = new Environment();
        e1.put("x", 1);

        Environment e2 = new Environment();
        e2.put("y", 2);

        assertEquals(env("x", 1, "y", 2), Environment.join(e1, e2));
    }

    @Test
    public void testJoinNegatives() {
        Environment e1 = new Environment();
        e1.put("x", new Environment.NegativeBinding(1));

        Environment e2 = new Environment();
        e2.put("x", new Environment.NegativeBinding(2));

        assertEquals(env("x", envNeg(1, 2)), Environment.join(e1, e2));
    }

    @Test
    public void testJoinMixed() {
        Environment e1 = new Environment();
        e1.put("x", new Environment.NegativeBinding(1));
        e1.put("y", 3);

        Environment e2 = new Environment();
        e2.put("x", new Environment.NegativeBinding(2));
        e2.put("z", 4);

        assertEquals(env("x", envNeg(1, 2), "y", 3, "z", 4), Environment.join(e1, e2));
    }

    @Test
    public void testNegationOfPositives() {
        Environment e1 = new Environment();
        e1.put("x", 1);
        e1.put("y", 2);

        assertEquals(envSet(env("x", envNeg(1), "y", envNeg(2))), Environment.negate(e1));
    }

    @Test
    public void testNegationOfSingularNegatives() {
        Environment e1 = new Environment();
        e1.put("x", new Environment.NegativeBinding(1));
        e1.put("y", new Environment.NegativeBinding(2));

        assertEquals(envSet(env("x", 1, "y", 2)), Environment.negate(e1));
    }

    @Test
    public void testNegationOfNonSingularNegatives() {
        Environment e1 = new Environment();
        e1.put("x", new Environment.NegativeBinding(1, 2));
        e1.put("y", new Environment.NegativeBinding(3, 4));

        assertEquals(envSet(env("x", 1, "y", 3),
                            env("x", 2, "y", 3),
                            env("x", 1, "y", 4),
                            env("x", 2, "y", 4)), Environment.negate(e1));
    }
}
