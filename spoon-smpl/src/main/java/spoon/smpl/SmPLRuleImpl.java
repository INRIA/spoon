package spoon.smpl;

import org.apache.commons.lang3.NotImplementedException;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.smpl.formula.Formula;
import spoon.smpl.formula.MetavariableConstraint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of SmPLRule.
 */
public class SmPLRuleImpl implements SmPLRule {
    /**
     * Create a new SmPLRule.
     *
     * @param source Plain text SmPL source code
     * @param matchTargetDSL Match target executable DSL AST
     * @param formula Formula of the rule
     * @param metavars Metavariable names and their respective constraints
     */
    public SmPLRuleImpl(String source, CtExecutable matchTargetDSL, Formula formula, Map<String, MetavariableConstraint> metavars) {
        this.source = source;
        this.matchTargetDSL = matchTargetDSL;
        this.formula = formula;
        this.metavars = metavars;
        this.name = null;
        this.grepPattern = null;

        methodsAdded = new ArrayList<>();
    }

    /**
     * Set the name of the rule.
     * @param name name of the rule, or null for anonymous rule
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the name of the rule.
     * @return name of the rule, or null if the rule is anonymous
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Get the formula of the rule.
     * @return formula of the rule
     */
    @Override
    public Formula getFormula() {
        return formula;
    }

    /**
     * Get the plain text SmPL source code of the rule.
     *
     * @return Plain text SmPL source code of the rule
     */
    @Override
    public String getSource() {
        return source;
    }

    /**
     * Get the match target executable AST in the SmPL Java DSL.
     *
     * @return Match target executable AST in the SmPL Java DSL
     */
    @Override
    public CtExecutable<?> getMatchTargetDSL() {
        return matchTargetDSL;
    }

    /**
     * Get the set of metavariable names and their respective constraints.
     * @return Metavariable names and their respective constraints
     */
    @Override
    public Map<String, MetavariableConstraint> getMetavariableConstraints() {
        return metavars;
    }

    /**
     * Get the current metavariable bindings.
     * @return Current metavariable bindings, or null if there are none
     */
    @Override
    public Map<String, Object> getMetavariableBindings() {
        throw new NotImplementedException("Not implemented");
    }

    /**
     * Reset the metavariable bindings.
     */
    @Override
    public void reset() {
        throw new NotImplementedException("Not implemented");
    }

    /**
     * Get methods added to parent class of a method matching the rule.
     *
     * @return Methods added to parent class of a method matching the rule
     */
    @Override
    public List<CtMethod<?>> getMethodsAdded() {
        return methodsAdded;
    }

    /**
     * Pre-scan a given executable to check if it could potentially match the rule.
     *
     * @param ctExecutable Executable to scan
     * @return True if there is a possibility the rule could match the executable, false otherwise
     */
    @Override
    public boolean isPotentialMatch(CtExecutable<?> ctExecutable) {
        if (grepPattern == null) {
            grepPattern = SmPLGrep.buildPattern(this);
        }

        return grepPattern.matches(ctExecutable.toString());
    }

    /**
     * Add method that should be added to parent class of a method matching the rule.
     */
    public void addAddedMethod(CtMethod<?> method) {
        methodsAdded.add(method);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("SmPLRule(name=")
                .append(name)
                .append(", metavars=")
                .append(metavars.toString())
                .append(", formula=")
                .append(formula.toString())
                .append(")");

        return sb.toString();
    }

    /**
     * Name of the rule.
     */
    private String name;

    /**
     * Plain text SmPL source code.
     */
    private String source;

    /**
     * Match target executable AST in the SmPL Java DSL.
     */
    private CtExecutable<?> matchTargetDSL;

    /**
     * Formula of the rule.
     */
    private final Formula formula;

    /**
     * Metavariable names and their respective constraints.
     */
    private final Map<String, MetavariableConstraint> metavars;

    /**
     * Methods that should be added to parent class of a method matching the rule.
     */
    private List<CtMethod<?>> methodsAdded;

    /**
     * SmPLGrep pattern used in isPotentialMatch.
     */
    private SmPLGrep.Pattern grepPattern;
}
