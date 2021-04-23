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
package spoon.smpl.formula;

import java.util.Map;

/**
 * A MetadataPredicate is an non-metavariable-parameterized predicate that represents a search for
 * states that export arbitrary metadata under a certain name (key), binding the metadata value to
 * a variable in the environment.
 * <p>
 * For example, picture a model M with states s1 and s2, both labeled as exporting the metadata key
 * "parent", with the values s1.parent = 1:int and s2.parent = 2:int.
 * <p>
 * We would then expect SAT(MetadataPredicate("p1", "parent")) = {..., (s1, {p1=1}, []),
 * (s2, {p1=2}, []), ...}
 * <p>
 * The predicate can be used for filtering results based on arbitrary metadata, as in the following
 * example:
 * And(MetadataPredicate("r1", "reachable"), SetEnv("r1", true))
 * <p>
 * This formula would select all states exporting the metadata key "reachable" with the specific
 * value of true:boolean.
 */
public class MetadataPredicate implements Predicate {
	/**
	 * Create a new metadata search predicate.
	 *
	 * @param varname Environment variable for storing found metadata values
	 * @param key     Metadata key to search for
	 */
	public MetadataPredicate(String varname, String key) {
		this.varname = varname;
		this.key = key;
	}

	/**
	 * Get the requested metadata key.
	 *
	 * @return Requested metadata key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Get the target environment variable name.
	 *
	 * @return Target environment variable name
	 */
	public String getVarname() {
		return varname;
	}

	/**
	 * MetadataPredicate does not use metavariables.
	 *
	 * @return null
	 */
	@Override
	public Map<String, MetavariableConstraint> getMetavariables() {
		return null;
	}

	/**
	 * MetadataPredicate does not use metavariables.
	 *
	 * @return true
	 */
	@Override
	public boolean processMetavariableBindings(Map<String, Object> parameters) {
		return true;
	}

	/**
	 * Implement the Visitor pattern.
	 *
	 * @param visitor Visitor to accept
	 */
	@Override
	public void accept(FormulaVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return "Metadata(" + key + "->" + varname + ")";
	}

	/**
	 * Target environment variable name.
	 */
	private String varname;

	/**
	 * Requested metadata key.
	 */
	private String key;
}
