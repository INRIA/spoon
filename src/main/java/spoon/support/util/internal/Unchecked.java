package spoon.support.util.internal;

/**
 * We do a bunch of unchecked casts in Spoon due to design decisions in the past. This class contains
 * helpers to hide some of this away, but not so well that we can't see that we're doing something bad.
 */
public class Unchecked {

    private Unchecked() {
    }

    /**
     * Make an unchecked cast of the provided object to the type of the expression it is used in.
     *
     * This should primarily be used to coerce <code>this</code> to the expected return type in methods that are
     * (unsafely) generic * in the return type.
     *
     * THIS IS VERY BAD, use only where legacy requires it to avoid having to litter classes with
     * <code>SuppressWarnings("unchecked")</code>
     */
    @SuppressWarnings("unchecked")
    public static <T> T castToReturnType(Object object) {
        return (T) object;
    }
}
