package spoon.smpl;

import spoon.reflect.declaration.CtElement;
import spoon.smpl.formula.Predicate;
import spoon.smpl.formula.VariableUsePredicate;
import spoon.smpl.pattern.PatternBuilder;
import spoon.smpl.pattern.PatternNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CodeElementLabel provides an abstract base class for labels over code elements that can be
 * matched with generic code-matching predicates such as VariableUsePredicate.
 */
abstract public class CodeElementLabel implements Label {
    /**
     * Create a new CodeElementLabel using a given code element.
     *
     * @param codeElement Code element to use.
     */
    public CodeElementLabel(CtElement codeElement) {
        this.codeElement = codeElement;

        PatternBuilder builder = new PatternBuilder();
        this.codeElement.accept(builder);
        this.codePattern = builder.getResult();

        this.metavarBindings = null;
    }

    /**
     * Test whether the label matches the given predicate.
     *
     * @param obj Predicate to test
     * @return True if the predicate matches the label, false otherwise
     */
    public boolean matches(Predicate obj) {
        if (obj instanceof VariableUsePredicate) {
            VariableUsePredicate vup = (VariableUsePredicate) obj;
            List<String> metakeys = new ArrayList<>(vup.getMetavariables().keySet());
            Map<String, CtElement> variablesUsed = new VariableUseScanner(codeElement, metakeys).getResult();

            if (vup.getMetavariables().containsKey(vup.getVariable())) {
                Environment.MultipleAlternativesPositiveBinding alternatives = new Environment.MultipleAlternativesPositiveBinding();

                for (String varname : variablesUsed.keySet()) {
                    Object processed = vup.getMetavariables().get(vup.getVariable()).apply(variablesUsed.get(varname));

                    if (processed != null) {
                        alternatives.add(processed);
                    }
                }

                if (alternatives.size() > 0) {
                    metavarBindings = new HashMap<>();
                    metavarBindings.put(vup.getVariable(), alternatives);
                    return true;
                } else {
                    return false;
                }
            } else {
                return variablesUsed.containsKey(vup.getVariable());
            }
        } else {
            return false;
        }
    }

    /**
     * Retrieve any metavariable bindings involved in matching the most recently given predicate.
     *
     * @return Most recent metavariable bindings, or null if there are none
     */
    @Override
    public Map<String, Object> getMetavariableBindings() {
        return metavarBindings;
    }

    /**
     * Reset/clear metavariable bindings
     */
    @Override
    public void reset() {
        metavarBindings = null;
    }

    /**
     * Get the code element.
     * @return The code element
     */
    public CtElement getCodeElement() {
        return codeElement;
    }

    @Override
    public String toString() {
        return codeElement.toString();
    }

    /**
     * The code element.
     */
    protected CtElement codeElement;

    /**
     * The pattern corresponding to the code element.
     * Part of temporary substitute for spoon.pattern.
     */
    protected PatternNode codePattern;

    /**
     * The most recently matched metavariable bindings.
     */
    protected Map<String, Object> metavarBindings;
}
