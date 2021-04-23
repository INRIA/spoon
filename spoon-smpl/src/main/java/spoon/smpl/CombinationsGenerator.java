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

import java.util.ArrayList;
import java.util.List;

/**
 * A CombinationsGenerator generates the set of unique combinations of picking one item from
 * each of N sets of items, also known as the cartesian product of sets. The implementation is
 * modeled after a traditional mechanical counter with a number of wheels. Incrementing the
 * counter spins the leftmost wheel which may or may not result in looping back to the starting
 * point of the wheel, in which case the next wheel is progressed one step.
 *
 * @param <T> Type of individual items
 */
class CombinationsGenerator<T> {
	/**
	 * Create a new CombinationsGenerator.
	 */
	CombinationsGenerator() {
		wheels = new ArrayList<>();
		positions = new int[0];
		doneFirst = false;
	}

	/**
	 * Add a new set of items from which a single choice should be made in each combination.
	 *
	 * @param items Set of items
	 */
	public void addWheel(List<T> items) {
		wheels.add(items);
		positions = new int[wheels.size()];
		doneFirst = false;
	}

	/**
	 * Step to the next combination.
	 *
	 * @return True if the generator has found a new combination without looping back to the start, false otherwise.
	 */
	public boolean next() {
		if (!doneFirst) {
			doneFirst = true;
			return true;
		}

		for (int i = 0; i < positions.length; ++i) {
			positions[i] += 1;

			if (positions[i] < wheels.get(i).size()) {
				return true;
			} else {
				positions[i] = 0;
			}
		}

		return false;
	}

	/**
	 * Get the current combination of items.
	 *
	 * @return Current combination of items
	 */
	public List<T> current() {
		List<T> result = new ArrayList<>();

		for (int i = 0; i < positions.length; ++i) {
			result.add(wheels.get(i).get(positions[i]));
		}

		return result;
	}

	/**
	 * Sets of items.
	 */
	private final List<List<T>> wheels;

	/**
	 * Positions of each 'wheel'.
	 */
	private int[] positions;

	/**
	 * Has the (0, 0, ..., 0) combination been stepped past?
	 */
	private boolean doneFirst;
}
