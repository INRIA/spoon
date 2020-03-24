package spoon.smpl.metavars;

import spoon.reflect.reference.CtTypeReference;
import spoon.smpl.formula.ParameterPostProcessStrategy;

import java.util.Map;

public class TypeConstraint implements ParameterPostProcessStrategy {
    @Override
    public Boolean apply(Map<String, Object> parameters, String paramName) {
        Object obj = parameters.get(paramName);

        if (obj instanceof CtTypeReference) {
            return true;
        } else {
            return false;
        }
    }
}
