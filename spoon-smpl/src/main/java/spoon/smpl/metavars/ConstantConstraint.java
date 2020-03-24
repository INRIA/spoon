package spoon.smpl.metavars;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLiteral;
import spoon.smpl.formula.ParameterPostProcessStrategy;

import java.util.Map;

public class ConstantConstraint implements ParameterPostProcessStrategy {
    @Override
    public Boolean apply(Map<String, Object> parameters, String paramName) {
        Object obj = parameters.get(paramName);

        if (obj instanceof CtLiteral) {
            return true;
        } else if (obj instanceof CtExpression) {
            CtExpression<?> expr = (CtExpression<?>) obj;

            if (expr.getDirectChildren().get(0) instanceof CtLiteral) {
                parameters.put(paramName, expr.getDirectChildren().get(0));
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
