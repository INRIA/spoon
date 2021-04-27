/*
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package spoon.smpl;

import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.smpl.formula.Formula;
import spoon.smpl.formula.MetavariableConstraint;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * An SmPLRule represents a single named or anonymous SmPL rule consisting of a formula, a
 * set of metavariables with respective constraints, and a set of current metavariable bindings
 * from the most recently matched code.
 */
public interface SmPLRule {
	/**
	 * Set the name of the rule.
	 *
	 * @param name name of the rule, or null for anonymous rule
	 */
	void setName(String name);

	/**
	 * Get the name of the rule.
	 *
	 * @return name of the rule, or null if the rule is anonymous
	 */
	String getName();

	/**
	 * Get the formula of the rule.
	 *
	 * @return formula of the rule
	 */
	Formula getFormula();

	/**
	 * Get the plain text SmPL source code of the rule.
	 *
	 * @return Plain text SmPL source code of the rule
	 */
	String getSource();

	/**
	 * Get the match target executable AST in the SmPL Java DSL.
	 *
	 * @return Match target executable AST in the SmPL Java DSL
	 */
	CtExecutable<?> getMatchTargetDSL();

	/**
	 * Get the metavariable names and their respective constraints.
	 *
	 * @return Metavariable names and their respective constraints
	 */
	Map<String, MetavariableConstraint> getMetavariableConstraints();

	/**
	 * Get the current metavariable bindings.
	 *
	 * @return the current metavariable bindings, or null if there are none
	 */
	Map<String, Object> getMetavariableBindings();

	/**
	 * Reset the metavariable bindings.
	 */
	void reset();

	/**
	 * Get methods added to parent class of a method matching the rule.
	 *
	 * @return Methods added to parent class of a method matching the rule
	 */
	List<CtMethod<?>> getMethodsAdded();

	/**
	 * Scan a given executable to check if it could potentially match the rule.
	 * <p>
	 * The intention is to provide implementations with an opportunity to apply optimization pre-filtering, reducing
	 * the number of targets for full model checking.
	 *
	 * @param ctExecutable Executable to scan
	 * @return True if there is a possibility the rule could match the executable, false otherwise
	 */
	boolean isPotentialMatch(CtExecutable<?> ctExecutable);

	/**
	 * Scan a given File containing source code to check if it could potentially match the rule.
	 *
	 * @param sourceFile File containing Java source code
	 * @return True if there is a possibility the rule could match code in the File, false otherwise
	 */
	boolean isPotentialMatch(File sourceFile);
}
