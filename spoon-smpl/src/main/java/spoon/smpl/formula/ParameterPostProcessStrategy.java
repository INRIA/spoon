package spoon.smpl.formula;

import java.util.Map;
import java.util.function.BiFunction;

public interface ParameterPostProcessStrategy extends BiFunction<Map<String, Object>, String, Boolean> {
    @Override
    public Boolean apply(Map<String, Object> parameters, String paramName);
}
