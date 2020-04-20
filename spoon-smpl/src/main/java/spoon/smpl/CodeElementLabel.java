package spoon.smpl;

import spoon.reflect.declaration.CtElement;
import spoon.smpl.formula.Predicate;
import spoon.smpl.pattern.PatternBuilder;
import spoon.smpl.pattern.PatternNode;

import java.util.List;
import java.util.Map;

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
     * @return True if the predicate is a VariableUsePredicate which matches the label
     */
    public boolean matches(Predicate obj) {
        return false;
    }

    /**
     * Retrieve any metavariable bindings involved in matching the most recently
     * given predicate.
     *
     * @return Most recent metavariable bindings, or null if there were none
     */
    @Override
    public List<Map<String, Object>> getMetavariableBindings() {
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
    protected List<Map<String, Object>> metavarBindings;
}
