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

import spoon.smpl.operation.Operation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * An AnchoredOperationsMap is a Map from line numbers (integers) to Lists of Operations.
 */
public class AnchoredOperationsMap extends HashMap<Integer, List<Operation>> {
	/**
	 * Special key used for anchoring operations to the method body block.
	 */
	public static final Integer methodBodyAnchor = -1;

	/**
	 * Get operations anchored to the method body block, if any.
	 *
	 * @return List of operations, or null.
	 */
	public List<Operation> getOperationsAnchoredToMethodBody() {
		return getOrDefault(methodBodyAnchor, null);
	}

	/**
	 * Ensure a key exists, adding it if it does not.
	 *
	 * @param k Key which must exist
	 */
	public void addKeyIfNotExists(Integer k) {
		if (!containsKey(k)) {
			put(k, new ArrayList<>());
		}
	}

	/**
	 * Merge all contents from a second AnchoredOperationsMap map into
	 * this one.
	 *
	 * @param other AnchoredOperationsMap from which to merge all contents
	 */
	public void join(AnchoredOperationsMap other) {
		for (int k : other.keySet()) {
			addKeyIfNotExists(k);
			get(k).addAll(other.get(k));
		}
	}
}
