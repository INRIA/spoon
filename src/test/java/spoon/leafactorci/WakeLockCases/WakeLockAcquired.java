package spoon.leafactorci.WakeLockCases;

import spoon.leafactorci.engine.CaseOfInterest;
import spoon.leafactorci.engine.DetectionPhaseContext;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtVariableRead;

public class WakeLockAcquired extends CaseOfInterest {
    final public CtVariableRead variable;

    private WakeLockAcquired(CtVariableRead variable, DetectionPhaseContext context) {
        super(context);
        this.variable = variable;
    }

    public static WakeLockAcquired detect(DetectionPhaseContext context) {
        if (context.statement instanceof CtInvocation) {
            CtInvocation invocation = (CtInvocation) context.statement;
            if (!invocation.getExecutable().getSimpleName().equals("acquire")) {
                return null;
            }
            if (invocation.getTarget() instanceof CtVariableRead) {
                return new WakeLockAcquired((CtVariableRead) invocation.getTarget(), context);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "WakeLockAcquired";
    }
}