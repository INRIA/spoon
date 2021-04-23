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

import org.apache.commons.lang3.NotImplementedException;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.smpl.formula.Formula;
import spoon.smpl.formula.MetavariableConstraint;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
	 * @param source         Plain text SmPL source code
	 * @param matchTargetDSL Match target executable DSL AST
	 * @param formula        Formula of the rule
	 * @param metavars       Metavariable names and their respective constraints
	 */
	public SmPLRuleImpl(String source, CtExecutable<?> matchTargetDSL, Formula formula, Map<String, MetavariableConstraint> metavars) {
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
	 *
	 * @param name name of the rule, or null for anonymous rule
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the name of the rule.
	 *
	 * @return name of the rule, or null if the rule is anonymous
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Get the formula of the rule.
	 *
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
	 *
	 * @return Metavariable names and their respective constraints
	 */
	@Override
	public Map<String, MetavariableConstraint> getMetavariableConstraints() {
		return metavars;
	}

	/**
	 * Get the current metavariable bindings.
	 *
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
	 * Scan a given executable to check if it could potentially match the rule.
	 *
	 * @param ctExecutable Executable to scan
	 * @return True if there is a possibility the rule could match the executable, false otherwise
	 */
	@Override
	public boolean isPotentialMatch(CtExecutable<?> ctExecutable) {
		try {
			return isPotentialMatch(ctExecutable.getOriginalSourceFragment().getSourceCode());
		} catch (Exception ignored) {
			return isPotentialMatch(ctExecutable.toString());
		}
	}

	/**
	 * Scan a given File containing source code to check if it could potentially match the rule.
	 *
	 * @param sourceFile File containing Java source code
	 * @return True if there is a possibility the rule could match code in the File, false otherwise
	 */
	@Override
	public boolean isPotentialMatch(File sourceFile) {
		if (sourceFile == null) {
			throw new IllegalArgumentException("sourceFile cannot be null");
		}

		try {
			return isPotentialMatch(Files.readString(Paths.get(sourceFile.getPath()), StandardCharsets.UTF_8));
		} catch (Exception ignored) {
			System.err.println("WARNING: unable to read " + sourceFile.getPath());
			return true;
		}
	}

	/**
	 * Check if a given String, presumably containing source code, matches the SmPLGrep.Pattern of this rule.
	 *
	 * @param sourceCode String to check
	 * @return True if grep pattern matches the String, false otherwise
	 */
	private boolean isPotentialMatch(String sourceCode) {
		if (grepPattern == null) {
			grepPattern = SmPLGrep.buildPattern(this);
		}

		return grepPattern.matches(sourceCode);
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
