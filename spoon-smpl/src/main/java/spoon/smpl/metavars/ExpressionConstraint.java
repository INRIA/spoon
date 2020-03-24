package spoon.smpl.metavars;

import spoon.reflect.code.CtExpression;
import spoon.smpl.formula.ParameterPostProcessStrategy;

import java.util.Map;

public class ExpressionConstraint implements ParameterPostProcessStrategy {
    @Override
    public Boolean apply(Map<String, Object> parameters, String paramName) {
        Object obj = parameters.get(paramName);

        if (obj instanceof CtExpression) {
            return true;
        } else {
            return false;
        }
    }
}
