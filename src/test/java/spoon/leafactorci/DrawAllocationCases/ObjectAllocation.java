package spoon.leafactorci.DrawAllocationCases;

import spoon.leafactorci.engine.CaseOfInterest;
import spoon.leafactorci.engine.DetectionPhaseContext;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;

import java.util.List;

public class ObjectAllocation extends CaseOfInterest {
    final public CtVariableReference variable; // The variable that is being assigned
    final public CtConstructorCall constructorCall;

    private ObjectAllocation(CtVariableReference variable,
                             CtConstructorCall constructorCall,
                             DetectionPhaseContext context) {
        super(context);
        this.variable = variable;
        this.constructorCall = constructorCall;
    }

    public static boolean isClearable(CtTypeReference type) {
        return type.getSimpleName().startsWith("Collection") || type.getSimpleName().startsWith("java.util.List")
                || type.getSimpleName().startsWith("List") || type.getSimpleName().startsWith("java.util.List")
                || type.getSimpleName().startsWith("ArrayList") || type.getSimpleName().startsWith("java.util.ArrayList")
                || type.getSimpleName().startsWith("LinkedList") || type.getSimpleName().startsWith("java.util.LinkedList")
                || type.getSimpleName().startsWith("Vector") || type.getSimpleName().startsWith("java.util.Vector")
                || type.getSimpleName().startsWith("Stack") || type.getSimpleName().startsWith("java.util.Stack")
                || type.getSimpleName().startsWith("Set") || type.getSimpleName().startsWith("java.util.Set")
                || type.getSimpleName().startsWith("HashSet") || type.getSimpleName().startsWith("java.util.HashSet")
                || type.getSimpleName().startsWith("LinkedHashSet") || type.getSimpleName().startsWith("java.util.LinkedHashSet")
                || type.getSimpleName().startsWith("SortedSet") || type.getSimpleName().startsWith("java.util.SortedSet")
                || type.getSimpleName().startsWith("NavigableSet") || type.getSimpleName().startsWith("java.util.NavigableSet")
                || type.getSimpleName().startsWith("TreeSet") || type.getSimpleName().startsWith("java.util.TreeSet")
                || type.getSimpleName().startsWith("EnumSet") || type.getSimpleName().startsWith("java.util.EnumSet")
                || type.getSimpleName().startsWith("Queue") || type.getSimpleName().startsWith("java.util.Queue")
                || type.getSimpleName().startsWith("PriorityQueue") || type.getSimpleName().startsWith("java.util.PriorityQueue")
                || type.getSimpleName().startsWith("Deque") || type.getSimpleName().startsWith("java.util.Deque")
                || type.getSimpleName().startsWith("ArrayDeque") || type.getSimpleName().startsWith("java.util.ArrayDeque")
                || type.getSimpleName().startsWith("Map") || type.getSimpleName().startsWith("java.util.Map")
                || type.getSimpleName().startsWith("HashMap") || type.getSimpleName().startsWith("java.util.HashMap")
                || type.getSimpleName().startsWith("SortedMap") || type.getSimpleName().startsWith("java.util.SortedMap")
                || type.getSimpleName().startsWith("NavigableMap") || type.getSimpleName().startsWith("java.util.NavigableMap")
                || type.getSimpleName().startsWith("TreeMap") || type.getSimpleName().startsWith("java.util.TreeMap");
    }

    public static ObjectAllocation detect(DetectionPhaseContext context) {
        CtExpression assignmentExpression;
        CtVariableReference variableReference;
        if(context.statement instanceof CtVariable) {
            assignmentExpression = ((CtVariable)context.statement).getDefaultExpression();
            variableReference = ((CtVariable)context.statement).getReference();
        } else if (context.statement instanceof CtAssignment) {
            CtAssignment assignment = (CtAssignment)context.statement;
            assignmentExpression = assignment.getAssignment();
            CtExpression assignedExpression = assignment.getAssigned();
            if(!(assignedExpression instanceof CtVariableWrite)) {
                return null;
            }
            variableReference = ((CtVariableWrite) assignedExpression).getVariable();
        } else {
            return null;
        }

        CtConstructorCall constructorCall;
        if(assignmentExpression instanceof CtConstructorCall) {
            constructorCall = (CtConstructorCall) assignmentExpression;
        } else {
            return null;
        }

        // Remove allocation that depend on other variables
        List<CtExpression<?>> expressionList = constructorCall.getArguments();
        for(CtExpression expression : expressionList) {
            if(expression instanceof CtVariableRead) {
                return null;
            }
        }

        return new ObjectAllocation(variableReference, constructorCall, context);
    }

    @Override
    public String toString() {
        return "ObjectAllocation{" +
                "variable=" + variable +
                ", index=" + index +
                ", statementIndex=" + statementIndex +
                ", statement=" + statement +
                '}';
    }
}