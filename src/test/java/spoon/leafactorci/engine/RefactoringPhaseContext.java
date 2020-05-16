package spoon.leafactorci.engine;


import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the iteration context of the refactoring phase
 */
public class RefactoringPhaseContext {
    public int offset = 0;
    public CaseOfInterest caseOfInterest;
    public CtBlock block;
    public List<CaseOfInterest> casesOfInterest = new ArrayList<>();
    public Object extra;

    /**
     * The closest Block by bubbling up
     *
     * @return The closest Block by bubbling up
     */
    public CtBlock getClosestBlockParent() {
        return RefactoringRule.getClosestBlockParent(block);
    }

    /**
     * The closest Class by bubbling up
     *
     * @return The closest Class by bubbling up
     */
    public CtClass getClosestClassParent() {
        return RefactoringRule.getClosestClassParent(block);
    }

    /**
     * The closest Method by bubbling up
     *
     * @return The closest Method by bubbling up
     */
    public CtMethod getClosestMethodParent() {
        return RefactoringRule.getClosestMethodParent(block);
    }
}
