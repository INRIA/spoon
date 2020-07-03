package spoon.smpl;

import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.smpl.formula.Formula;
import spoon.smpl.formula.MetavariableConstraint;

import java.util.List;
import java.util.Map;

/**
 * An SmPLRule represents a single named or anonymous SmPL rule consisting of a formula, a
 * set of metavariables with respective constraints, and a set of current metavariable bindings
 * from the most recently matched code.
 */
public interface SmPLRule {
    // public getMetavariableBindings
    // public reset

    /**
     * Set the name of the rule.
     * @param name name of the rule, or null for anonymous rule
     */
    public void setName(String name);

    /**
     * Get the name of the rule.
     * @return name of the rule, or null if the rule is anonymous
     */
    public String getName();

    /**
     * Get the formula of the rule.
     * @return formula of the rule
     */
    public Formula getFormula();

    /**
     * Get the plain text SmPL source code of the rule.
     *
     * @return Plain text SmPL source code of the rule
     */
    public String getSource();

    /**
     * Get the match target executable AST in the SmPL Java DSL.
     *
     * @return Match target executable AST in the SmPL Java DSL
     */
    public CtExecutable<?> getMatchTargetDSL();

    /**
     * Get the metavariable names and their respective constraints.
     * @return Metavariable names and their respective constraints
     */
    public Map<String, MetavariableConstraint> getMetavariableConstraints();

    /**
     * Get the current metavariable bindings.
     * @return the current metavariable bindings, or null if there are none
     */
    public Map<String, Object> getMetavariableBindings();

    /**
     * Reset the metavariable bindings.
     */
    public void reset();

    /**
     * Get methods added to parent class of a method matching the rule.
     *
     * @return Methods added to parent class of a method matching the rule
     */
    public List<CtMethod<?>> getMethodsAdded();

    /**
     * Pre-scan a given executable to check if it could potentially match the rule.
     *
     * The intention is to provide implementations with an opportunity to apply optimization pre-filtering, reducing
     * the number of targets for full model checking.
     *
     * @param ctExecutable Executable to scan
     * @return True if there is a possibility the rule could match the executable, false otherwise
     */
    public boolean isPotentialMatch(CtExecutable<?> ctExecutable);
}
