package fr.inria.spoon.dataflow.misc;

import fr.inria.spoon.dataflow.memory.Memory;
import com.microsoft.z3.Expr;
import spoon.reflect.reference.CtReference;

import java.util.Map;

/**
 * Represents variablesMap and memory state in a specific branch.
 */
public class BranchData
{
    private Map<CtReference, Expr> variablesMap;
    private Memory memory;

    public BranchData(Map<CtReference, Expr> variablesMap, Memory memory)
    {
        this.variablesMap = variablesMap;
        this.memory = memory;
    }

    public Map<CtReference, Expr> getVariablesMap()
    {
        return variablesMap;
    }

    public Memory getMemory()
    {
        return memory;
    }
}
