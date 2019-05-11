package fr.inria.spoon.dataflow.misc;

import spoon.support.reflect.reference.CtVariableReferenceImpl;

/**
 * Artificial CtVariableReference to represent flags like continueFlagReference, breakFlagReference and so on.
 */
public class FlagReference extends CtVariableReferenceImpl<Boolean>
{
    private static int counter = 0;

    public FlagReference(String flagName)
    {
        setSimpleName(flagName);
        setType(getFactory().Type().BOOLEAN);
    }

    public static void resetCounter()
    {
        counter = 0;
    }

    public static FlagReference makeFreshReturnReference()
    {
        return new FlagReference("#NORETURN_FLAG_" + counter++);
    }

    public static FlagReference makeFreshBreakReference()
    {
        return new FlagReference("#BREAK_FLAG_" + counter++);
    }

    public static FlagReference makeFreshContinueReference()
    {
        return new FlagReference("#CONTINUE_FLAG_" + counter++);
    }

    public static FlagReference makeFreshThrowReference()
    {
        return new FlagReference("#THROW_FLAG_" + counter++);
    }


}
