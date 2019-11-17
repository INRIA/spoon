package fr.inria.spoon.dataflow.memory;

import com.microsoft.z3.*;
import spoon.reflect.reference.*;

import java.util.HashMap;
import java.util.Map;

import static fr.inria.spoon.dataflow.utils.TypeUtils.*;

/**
 * Represents a type-indexed memory model (Burstall’s memory model).
 * In this model each data type or field has its own memory array.
 */
public class Memory
{
    // Maps type reference or field reference to the corresponding memory array
    private Map<CtReference, ArrayExpr> memoryMap = new HashMap<>();

    // Represents a pointer to the next free memory address
    private int memoryPointer = 2;

    // Z3 solver context
    private final Context context;

    public Map<CtReference, ArrayExpr> getMemoryMap()
    {
        return memoryMap;
    }

    public Memory(Context context)
    {
        this.context = context;
    }

    public Memory(Memory other)
    {
        this.context = other.context;
        this.memoryMap = new HashMap<>(other.memoryMap);
        this.memoryPointer = other.memoryPointer;
    }

    /**
     * Ensures that corresponding memory array is created, or creates it otherwise.
     */
    private void ensureArrayCreated(CtReference reference)
    {
        // Create memory array for the reference if it does not exist
        if (memoryMap.get(reference) == null)
        {
            ArrayExpr memoryArray;
            ArraySort sort;
            if (reference instanceof CtArrayTypeReference)
            {
                ArraySort arraySort = context.mkArraySort(context.mkBitVecSort(32), getTypeSort(context, ((CtArrayTypeReference) reference).getComponentType()));
                sort = context.mkArraySort(context.mkIntSort(), arraySort);
            }
            else
            {
                CtTypeReference typeReference = reference instanceof CtVariableReference ? ((CtVariableReference) reference).getType() : (CtTypeReference) reference;
                sort = context.mkArraySort(context.getIntSort(), getTypeSort(context, typeReference));
            }
            memoryArray = (ArrayExpr) context.mkFreshConst(reference + "_memo_array_", sort);
            memoryMap.put(reference, memoryArray);
        }
    }

    /**
     * Reads from the memory array of the specified type at the index targetExpr.
     */
    public Expr read(CtReference type, IntExpr targetExpr)
    {
        ensureArrayCreated(type);
        ArrayExpr memoryArray = memoryMap.get(type);
        return context.mkSelect(memoryArray, targetExpr);
    }

    /**
     * Reads from the memory array of the specified array type at the index targetExpr and at the arrayIndex position.
     */
    public Expr readArray(CtArrayTypeReference type, IntExpr targetExpr, Expr arrayIndex)
    {
        ensureArrayCreated(type);
        ArrayExpr memoryArray = memoryMap.get(type);
        ArrayExpr arrayValue = (ArrayExpr) context.mkSelect(memoryArray, targetExpr);
        return context.mkSelect(arrayValue, arrayIndex);
    }

    /**
     * Writes value to the memory array of the specified type at the index targetExpr.
     */
    public void write(CtReference type, IntExpr targetExpr, Expr value)
    {
        ensureArrayCreated(type);
        ArrayExpr memoryArray = memoryMap.get(type);
        memoryMap.put(type, context.mkStore(memoryArray, targetExpr, value));
    }

    /**
     * Writes value to the memory array of the specified array type at the index targetExpr and at the arrayIndex position.
     */
    public void writeArray(CtArrayTypeReference type, IntExpr targetExpr, Expr arrayIndex, Expr value)
    {
        ensureArrayCreated(type);
        ArrayExpr memoryArray = memoryMap.get(type);
        ArrayExpr oldArrayValue = (ArrayExpr) context.mkSelect(memoryArray, targetExpr);
        ArrayExpr newArrayValue = context.mkStore(oldArrayValue, arrayIndex, value);
        memoryMap.put(type, context.mkStore(memoryArray, targetExpr, newArrayValue));
    }

    /**
     * Returns a pointer to the next free memory address and increments is.
     * Essentially it represents an allocation.
     */
    public int nextPointer()
    {
        return memoryPointer++;
    }

    /**
     * Returns null pointer.
     */
    public static int nullPointer()
    {
        return 0;
    }

    /**
     * Returns this pointer.
     */
    public static int thisPointer()
    {
        return 1;
    }

    /**
     * Resets the value of the object of specified type and address.
     * (It resets its fields, but not the value of the reference.)
     */
    public void resetObject(CtTypeReference type, IntExpr address)
    {
        // Reset fields
        for (Map.Entry<CtReference, ArrayExpr> entry : memoryMap.entrySet())
        {
            CtReference reference = entry.getKey();
            if (reference instanceof CtFieldReference)
            {
                if (((CtFieldReference) reference).getDeclaringType().equals(type))
                {
                    Sort sort = getTypeSort(context, ((CtFieldReference) reference).getType());
                    write(reference, address, context.mkFreshConst("", sort));
                }
            }
        }

        // Reset calculable value
        if (isCalculable(type))
        {
            Sort sort = getTypeSort(context, type.unbox());
            write(type.unbox(), address, context.mkFreshConst("", sort));
        }
    }

    /**
     * Completely resets memory.
     */
    public void reset()
    {
        memoryMap.clear();
        memoryPointer = 2;
    }

    /**
     * Resets all mutable elements in memory.
     */
    public void resetMutable()
    {
        memoryMap.entrySet().removeIf(e -> !(e.getKey() instanceof CtFieldReference
                                             && ((CtFieldReference) e.getKey()).isFinal()));
    }
}
