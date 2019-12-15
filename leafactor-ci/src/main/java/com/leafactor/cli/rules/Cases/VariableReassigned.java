package com.leafactor.cli.rules.Cases;

import com.leafactor.cli.engine.CaseOfInterest;
import com.leafactor.cli.engine.DetectionPhaseContext;
import spoon.reflect.code.CtAssignment;

public class VariableReassigned extends CaseOfInterest {
    final public CtAssignment assignment;

    private VariableReassigned(CtAssignment assignment, DetectionPhaseContext context) {
        super(context);
        this.assignment = assignment;
    }

    public static VariableReassigned detect(DetectionPhaseContext context) {
        if (context.statement instanceof CtAssignment) {
            CtAssignment assignment = (CtAssignment) context.statement;
            return new VariableReassigned(assignment, context);
        }
        return null;
    }

    @Override
    public String toString() {
        return "VariableReassigned{" +
                "assignment=" + assignment +
                ", index=" + index +
                ", statementIndex=" + statementIndex +
                ", statement=" + statement +
                '}';
    }
}