package spoon.smpl.formula;

//import spoon.pattern.Pattern;
import org.apache.commons.lang3.NotImplementedException;
import spoon.reflect.declaration.CtElement;
import spoon.smpl.pattern.PatternNode;

import java.util.Map;

/**
 * A BranchPattern is a Predicate that contains a Spoon Pattern (temporarily
 * substituted for an internal pattern matching mechanism) and a type identifier
 * indicating the type of the branch statement element (e.g CtIf.class)
 *
 * The intention is for a BranchPattern to contain a Pattern that corresponds to
 * the conditional expression used in a given branch statement element such as CtIf,
 * but the current implementation does not enforce this.
 */
public class BranchPattern implements Predicate {
    public BranchPattern(PatternNode cond, Class<? extends CtElement> branchType) {
        this(cond, branchType, null);
    }
    /**
     * Create a new BranchPattern Predicate.
     * @param cond The pattern to match (against the condition)
     */
    public BranchPattern(PatternNode cond, Class<? extends CtElement> branchType, Map<String, ParameterPostProcessStrategy> paramStrats) {
        this.cond = cond;
        this.branchType = branchType;
        this.paramStrats = paramStrats;
    }

    /**
     * @return the Pattern to match
     */
    public PatternNode getConditionPattern() {
        return cond;
    }

    /**
     * @return the type of the branch statement element
     */
    public Class<? extends CtElement> getBranchType() {
        return branchType;
    }

    /**
     * Implements the Visitor pattern.
     * @param visitor Visitor to accept
     */
    @Override
    public void accept(FormulaVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean processParameterBindings(Map<String, Object> parameters) {
        if (paramStrats == null) {
            return true;
        }

        for (String key : paramStrats.keySet()) {
            if (parameters.containsKey(key) && !paramStrats.get(key).apply(parameters, key)) {
                return false;
            }
        }

        return true;
    }

    /**
     * The Pattern to match.
     */
    private PatternNode cond;

    /**
     * The type of the branch statement element.
     */
    private Class<? extends CtElement> branchType;

    private Map<String, ParameterPostProcessStrategy> paramStrats;
}
