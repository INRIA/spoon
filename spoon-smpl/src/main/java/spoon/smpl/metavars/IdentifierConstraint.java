package spoon.smpl.metavars;

import spoon.reflect.code.CtVariableRead;
import spoon.reflect.reference.CtVariableReference;
import spoon.smpl.formula.ParameterPostProcessStrategy;

import java.util.Map;

public class IdentifierConstraint implements ParameterPostProcessStrategy {
    @Override
    public Boolean apply(Map<String, Object> parameters, String paramName) {
        Object obj = parameters.get(paramName);

        if (obj instanceof CtVariableReference) {
            return true;
        } else if (obj instanceof CtVariableRead) {
            parameters.put(paramName, ((CtVariableRead) obj).getVariable());
            return true;
        } else {
            return false;
        }
    }
}
