package spoon.smpl;

import spoon.reflect.declaration.CtElement;
import spoon.smpl.formula.Expression;
import spoon.smpl.formula.Predicate;
import spoon.smpl.formula.VariableUsePredicate;
import spoon.smpl.pattern.PatternBuilder;
import spoon.smpl.pattern.PatternNode;
import spoon.smpl.pattern.SubElemPatternMatcher;

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

        reset();
    }

    /**
     * Test whether the label matches the given predicate.
     *
     * @param predicate Predicate to test
     * @return True if the predicate matches the label, false otherwise
     */
    @Override
    public boolean matches(Predicate predicate) {
        if (predicate instanceof Expression) {
            Expression ep = (Expression) predicate;

            SubElemPatternMatcher spm = new SubElemPatternMatcher(ep.getPattern());

            if (spm.matches(codePattern)) {
                for (SubElemPatternMatcher.MatchResult result : spm.getResult()) {
                    if (ep.processMetavariableBindings((result.parameters))) {
                        matchResults.add(new LabelMatchResultImpl(result.matchedElement, result.parameters));
                    }
                }

                return true;
            }

            return false;
        } else if (predicate instanceof VariableUsePredicate) {
            VariableUsePredicate vup = (VariableUsePredicate) predicate;
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
                    HashMap<String, Object> metavarBindings = new HashMap<>();
                    metavarBindings.put(vup.getVariable(), alternatives);
                    matchResults.add(new LabelMatchResultImpl(metavarBindings));
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
     * Reset/clear any match results.
     */
    @Override
    public void reset() {
        matchResults = new ArrayList<>();
    }

    /**
     * Get the code element.
     *
     * @return The code element
     */
    public CtElement getCodeElement() {
        return codeElement;
    }

    /**
     * Get the match results produced for the most recently matched Predicate.
     *
     * @return List of results
     */
    @Override
    public List<LabelMatchResult> getMatchResults() {
        return matchResults;
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
     * List of match results produced from the most recently matching Predicate.
     */
    protected List<LabelMatchResult> matchResults;
}
