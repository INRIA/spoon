package spoon.leafactorci.engine;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the context of an iteration transformation phase
 */
public class TransformationPhaseContext {
    public CtBlock block;
    public int statementIndex;
    public CtStatement statement;
    public CaseOfInterest caseOfInterest;
    public List<CaseOfInterest> caseOfInterestList = new ArrayList<>();
    private List<CaseOfInterest> transformedCaseOfInterestList = new ArrayList<>();
    public Object extra;

    /**
     * Accepts the case for the transformation phase
     */
    public void accept(CaseOfInterest caseOfInterest) {
        if(!transformedCaseOfInterestList.contains(caseOfInterest)) {
            transformedCaseOfInterestList.add(caseOfInterest);
        }
    }

    public List<CaseOfInterest> getResult() {
        return new ArrayList<>(transformedCaseOfInterestList);
    }

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