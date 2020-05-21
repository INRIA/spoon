package spoon.smpl;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

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

        assertEquals(envSet(env("x", 1, "y", envNeg(2)), env("x", envNeg(1), "y", 2)), Environment.negate(e1));
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

    @Test
    public void testNegationOfBottom() {

        // contract: the negation of the bottom/conflicting environment is the any-environment

        assertEquals(new HashSet<>(Arrays.asList(new Environment())), Environment.negate(null));
    }

    @Test
    public void testJoinBottom() {

        // contract: if either of the two environments being joined is conflicting, the result is conflicting

        Environment environment = new Environment();

        assertEquals(null, Environment.join(environment, null));
        assertEquals(null, Environment.join(null, environment));
        assertEquals(null, Environment.join(null, null));
    }

    @Test
    public void testRejectDueToNegativeBinding() {

        // contract: the join of two envs. where a negative binding in one matches a positive binding in the other is conflicting

        Environment e1 = new Environment();
        Environment e2 = new Environment();

        e1.put("x", 1);
        e2.put("x", new Environment.NegativeBinding(1));

        assertEquals(null, Environment.join(e1, e2));
        assertEquals(null, Environment.join(e2, e1));
    }

    @Test
    public void testJoinDoesNotMutateInputs() {

        // contract: Environment.join(a,b) should not mutate a or b

        Environment e1 = new Environment();
        Environment e2 = new Environment();

        e1.put("x", new Environment.NegativeBinding("y"));
        e2.put("x", new Environment.NegativeBinding("z"));

        // TODO: use hashCodes
        String e1str = e1.toString();
        String e2str = e2.toString();

        Environment e3 = Environment.join(e1, e2);

        assertEquals(e1str, e1.toString());
        assertEquals(e2str, e2.toString());
    }
}
