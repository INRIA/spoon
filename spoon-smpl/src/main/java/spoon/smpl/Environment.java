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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * An Environment is a key-value store for metavariable bindings in the CTL-VW model checking procedure.
 */
public class Environment extends HashMap<String, Object> {
	/**
	 * A MultipleAlternativesPositiveBinding offers a way to store an entry with multiple alternative
	 * acceptable bindings in the environment that when negated will produce a single NegativeBinding
	 * containing all the alternatives in a single environment.
	 * <p>
	 * Using this class only makes sense when the result is guaranteed to be negated, as
	 * is currently the case in the context of VariableUsePredicates that will currently only
	 * ever appear in the form Not(Used(x)) and that often need to generate multiple possible
	 * bindings for x.
	 * <p>
	 * Examples:    P[a,b,c] is a MultipleAlternativesPositiveBinding,   [a,b,c] is a NegativeBinding
	 * <p>
	 * input:          negation:
	 * (x=P[a,b,c])     {(x=[a,b,c])}
	 * (x=[a,b,c])      {(x=a),(x=b),(x=c)}        note that negation is not an inverse operation here
	 */
	public static class MultipleAlternativesPositiveBinding extends HashSet<Object> {
		/**
		 * Create a new MultipleAlternativesPositiveBinding over a set of acceptable bindings.
		 *
		 * @param xs Values of acceptable bindings
		 * @return Instance of MultipleAlternativesPositiveBinding covering all given acceptable bindings
		 */
		public static MultipleAlternativesPositiveBinding create(Object... xs) {
			MultipleAlternativesPositiveBinding result = new MultipleAlternativesPositiveBinding();
			result.addAll(Arrays.asList(xs));
			return result;
		}

		@Override
		public String toString() {
			return "P" + super.toString();
		}

		@Override
		public int hashCode() {
			return 23 * super.hashCode();
		}

		@Override
		public boolean equals(Object other) {
			return this == other || (other instanceof MultipleAlternativesPositiveBinding && hashCode() == other.hashCode());
		}
	}

	/**
	 * A NegativeBinding represents a set of unacceptable bindings for some metavariable.
	 */
	public static class NegativeBinding extends HashSet<Object> {
		/**
		 * Create a new NegativeBinding over a set of unacceptable bindings.
		 *
		 * @param xs Values of unacceptable bindings
		 */
		public NegativeBinding(Object... xs) {
			super();
			this.addAll(Arrays.asList(xs));
		}

		@Override
		public int hashCode() {
			return 17 * super.hashCode();
		}

		@Override
		public boolean equals(Object other) {
			return this == other || (other instanceof NegativeBinding && hashCode() == other.hashCode());
		}
	}

	/**
	 * Create a new Environment.
	 */
	public Environment() {
		super();
	}

	/**
	 * Create a new Environment by copying all key-value pairs of an existing Environment.
	 *
	 * @param e Environment to copy
	 */
	public Environment(Environment e) {
		super();
		putAll(e);
	}

	/**
	 * Compute the join (union) of two given environments.
	 *
	 * @param e1 First environment
	 * @param e2 Second environment
	 * @return the join of the environments
	 */
	public static Environment join(Environment e1, Environment e2) {
		if (e1 == null || e2 == null) {
			return null;
		}

		Environment result = e1.clone();

		for (String key : e2.keySet()) {
			if (result.containsKey(key)) {
				Object v1 = result.get(key);
				Object v2 = e2.get(key);

				if (v1 instanceof NegativeBinding) {
					NegativeBinding negv1 = (NegativeBinding) v1;

					if (v2 instanceof NegativeBinding) {
						negv1.addAll((NegativeBinding) v2);
					} else {
						if (negv1.contains(v2)) {
							// negative binding in e1 rules out value bound in e2 -> incompatible
							return null;
						}
					}
				} else {
					if (v2 instanceof NegativeBinding) {
						NegativeBinding negv2 = (NegativeBinding) v2;

						if (negv2.contains(v1)) {
							// negative binding in e2 rules out value bound in e1 -> incompatible
							return null;
						}
					} else {
						if (!v1.equals(v2)) {
							// mismatched positive bindings -> incompatible
							return null;
						}
					}
				}
			} else {
				result.put(key, e2.get(key));
			}
		}

		return result;
	}

	/**
	 * Compute the negation of a given environment. Note that the result is a set of alternatives rather
	 * than a single environment. The reason for this is that a negative binding can reference multiple
	 * values that when negated produces one alternative for each value.
	 *
	 * @param e Environment to negate
	 * @return Set of alternatives representing the negation of the given environment
	 */
	public static Set<Environment> negate(Environment e) {
		Set<Environment> result = new HashSet<>();

		// The negation of the bottom/conflicting environment is the top/any-environment
		if (e == null) {
			result.add(new Environment());
			return result;
		}

		// The negation of the top/any-environment is the bottom/conflicting environment
		if (e.isEmpty()) {
			return null;
		}

		// See invariant comment below
		Queue<Environment> workqueue = new LinkedList<>();

		// Negate positive bindings
		for (String key : e.keySet()) {
			Object val = e.get(key);

			if (!(val instanceof NegativeBinding)) {
				NegativeBinding binding = new NegativeBinding();

				if (val instanceof MultipleAlternativesPositiveBinding) {
					binding.addAll((MultipleAlternativesPositiveBinding) val);
				} else {
					binding.add(val);
				}

				Environment env = new Environment();
				env.put(key, binding);

				// TODO: properly figure out if this bit should be included or not
				// Note: this adds all -other- bindings (excluding the key processed by the
				//       outermost loop) as-is to the env placed in the workqueue.
				/*for (String key2 : e.keySet()) {
					if (key2.equals(key)) {
						continue;
					}

					env.put(key2, e.get(key2));
				}*/

				workqueue.add(env);
			}
		}

		if (workqueue.isEmpty()) {
			workqueue.add(new Environment());
		}

		// Negate negative bindings
		for (String key : e.keySet()) {
			Object val = e.get(key);

			// Invariant: workqueue contains every base environment needed to produce global alternatives
			//            when processing the next negative binding, including all the alternatives produced by
			//            previously processed negative bindings.
			//
			//            Each negative binding must produce one local alternative for each value in its binding,
			//            and each of these local alternatives must be appended to every global alternative (every
			//            entry in the workqueue) to produce the workqueue for the next iteration.
			//
			//            For example, if the workqueue contains {x=1} and we process the negative binding y=[2,3],
			//            the resulting workqueue becomes [{x=1,y=2}, {x=1,y=3}], and if we then process z=[4,5] we
			//            get [{x=1,y=2,z=4}, {x=1,y=3,z=4}, {x=1,y=2,z=5}, {x=1,y=3,z=5}]

			if (val instanceof NegativeBinding) {
				NegativeBinding negativeBinding = (NegativeBinding) val;
				int numQueued = workqueue.size();

				for (int i = 0; i < numQueued; ++i) {
					Environment base = workqueue.poll();

					for (Object innerVal : negativeBinding) {
						Environment newEnv = new Environment(base);
						newEnv.put(key, innerVal);
						workqueue.add(newEnv);
					}
				}
			}
		}

		result.addAll(workqueue);
		return result;
	}

	@Override
	public Environment clone() {
		Environment result = new Environment();

		for (String key : keySet()) {
			Object obj = get(key);

			if (obj instanceof NegativeBinding) {
				result.put(key, ((NegativeBinding) obj).clone());
			} else {
				result.put(key, obj);
			}
		}

		return result;
	}
}
