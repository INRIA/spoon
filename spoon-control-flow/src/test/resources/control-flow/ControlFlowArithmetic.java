/**
 * The MIT License
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package fr.inria.juncoprovider.testproject;

import java.util.List;

/**
 * Created by marcel on 23/02/14.
 * <p/>
 * A class to test some coverage. In some method an "explosive" line is introduced
 * which will not be tested.
 */
public class ControlFlowArithmetic {

	/**
	 * A simple break to example
	 *
	 * @return
	 */
	public int simpleContinueTo() {
		int i = 0;
		outer_loop:
		while (i < 100) {
			int k = 0;
			while (k < 20) {
				k += 3;
				i = k + 5;
				if (i % 2 == 0) continue outer_loop;
			}
		}
		return i;
	}

	//several constructions one after the other
	public int mixed(int a, int b) {
		if (b % 2 == 0) {
			return a + b;
		}

		for (int i = 0; i < a; i++) b += a * b;

		return a + b * 2;
	}

	//several constructions one after the other
	public int invocation(int a, int b) {
		if (b % 2 == 0) {
			return nestedIfs(a, b);
		}
		return a + b * 2;
	}

	/**
	 * Simple example of break
	 *
	 * @param args
	 */
	public boolean simpleBreakLabeled(String[] args) {

		int[][] arrayOfInts = {
				{32, 87, 3, 589},
				{12, 1076, 2000, 8},
				{622, 127, 77, 955}
		};
		int searchfor = 12;

		int i;
		int j = 0;
		boolean foundIt = false;

		search:
		for (i = 0; i < arrayOfInts.length; i++) {
			for (j = 0; j < arrayOfInts[i].length; j++) {
				if (arrayOfInts[i][j] == searchfor) {
					foundIt = true;
					break search;
				}
			}
		}
		return foundIt;
	}

	/**
	 * Simple example of break
	 *
	 * @param args
	 */
	public boolean simpleBreak(String[] args) {

		int[][] arrayOfInts = {
				{32, 87, 3, 589},
				{12, 1076, 2000, 8},
				{622, 127, 77, 955}
		};
		int searchfor = 12;

		int i;
		int j = 0;
		boolean foundIt = false;
		for (i = 0; i < arrayOfInts.length; i++) {
			for (j = 0; j < arrayOfInts[i].length; j++) {
				if (arrayOfInts[i][j] == searchfor) {
					foundIt = true;
					break;
				}
			}
		}
		return foundIt;
	}


	//complex case of continue and breaks.
	public void continueAndBreak() {
		factorize_loop:
		while (true) {
			int j = 0;
			j++;
			int ntry = 9;
			int[] factors = new int[6];
			if (j <= 4)
				ntry = factors[j - 1];
			else
				ntry += 2;
			int nl = 5;
			do {
				int nq = nl / ntry;
				int nr = nl - ntry * nq;
				if (nr != 0)
					continue factorize_loop;
				int nf = 8;
				nf++;
				int[] wtable = new int[6];
				int fourn = 0;
				wtable[nf + 1 + fourn] = ntry;
				nl = nq;
				if (ntry == 2 && nf != 1) {
					int i;
					for (i = 2; i <= nf; i++) {
						int ib = nf - i + 2;
						int idx = ib + fourn;
						wtable[idx + 1] = wtable[idx];
					}
					wtable[2 + fourn] = 2;
				}
			} while (nl != 1);
			break factorize_loop;
		}
	}

	//Some nested ifs some not returning
	public int nestedIfSomeNotReturning(int a, int b) {
		if (a > 0) {
			if (b > 0) return a * b;
		} else {
			if (b < 1) return a * b * b;
			else {
				a = a * b;
				b = b * b;
			}
		}
		return b;
	}

	//Some nested ifs
	public int nestedIfAllReturning(int a, int b) {
		if (a > 0) {
			if (b > 0) return a * b;
			else return 0;
		} else {
			if (b < 1) return a * b * b;
			else {
				a = a * b;
				b = b * b;
				return b;
			}
		}
		//return 0;
	}

	//Some nested ifs
	public int nestedIfs(int a, int b) {
		if (a > 0) {
			if (b > 0) return a * b;
		} else {
			if (b < 1) return a * b * b;
			else {
				a = a * b;
				b = b * b;
				return b;
			}
		}
		return 0;
	}

	public void dontReturn(int a) {
		if (a > 0) System.out.print("otra cosa");
		else System.out.print("A < 0!");
	}

	public void returnVoid(int a) {
		if (a > 0) return;
		else System.out.print("A < 0!");
	}

	public int nestedConditional(int a) {
		int k = a / 2;
		int b = a > 0 ? k < 4 ? a * a : 8 : -a * a;
		return b;
	}

	public int conditional(int a) {
		int b = a > 0 ? a * a : -a * a;
		return b;
	}

	/**
	 * A method with a while to test the control flow
	 */
	public int ctDoWhile(List<Integer> a) {
		int b = 0;
		int i = 0;
		do b += i++ * b; while (i < a.size());
		return b;
	}

	/**
	 * A method with a while to test the control flow
	 */
	public int ctDoWhileBlock(List<Integer> a) {
		int b = 0;
		int i = 0;
		do {
			int k = i * i;
			b += i++ * b;
		} while (i < a.size());
		return b;
	}


	/**
	 * A method with a while to test the control flow
	 */
	public int ctWhile(List<Integer> a) {
		int b = 0;
		int i = 0;
		while (i < a.size()) b += i++ * b;
		return b;
	}

	/**
	 * A method with a while to test the control flow
	 */
	public int ctWhileBlock(List<Integer> a) {
		int b = 0;
		int i = 0;
		while (i < a.size()) {
			b += i * b;
			i++;
		}
		return b;
	}


	/**
	 * A method with a foreach to tes the control flow
	 */
	public int ctForEach(List<Integer> a) {
		int b = 0;
		for (int i : a) b += i * b;
		return b;
	}

	/**
	 * A method with a foreach to tes the control flow
	 */
	public int ctForEachBlock(List<Integer> a) {
		int b = 0;
		for (int i : a) {
			int k = i * i;
			b += k * b;
		}
		return b;
	}

	//A For to test the control flow in a for
	public int ctFor(int a, int b) {
		for (int i = 0; i < a; i++) b += a * b;
		return b;
	}

	//A For to test the control flow in a for
	public int ctForBlock(int a, int b) {
		for (int i = 0; i < a; i++) {
			int k = i * i;
			b += a * b + i;
		}
		return b;
	}

	//Yet another dummy procedure to test some logic branches
	public int ifThen(int a, int b) {
		if (b % 2 == 0) return a - b;
		return 0;
	}

	//Yet another dummy procedure to test some logic branches
	public int ifThenElse(int a, int b) {
		if (b % 2 == 0) return a - b;
		else b = b + a;
		return b * b;
	}


	//Yet another dummy procedure to test some logic branches
	public int ifThenBlock(int a, int b) {
		if (b % 2 == 0) {
			a += b * b;
			return a - b;
		}
		return 0;
	}

	//Yet another dummy procedure to test some logic branches
	public int ifThenElseBlock(int a, int b) {
		if (b % 2 == 0) {
			a += b * b;
			return a - b;
		} else {
			return a - b * 2;
		}
	}

	public int switchTest(int a) {
		int b = 0;
		switch (a) {
			case 1:
				return 0;
			case 2:
				b = a * 2;
				break;
			case 3:
			case 4:
				b = a * 9;
				break;
			default:
				return 0;
		}
		return b;
	}

	public int lastCaseFallThrough(int a) {
		int b = 0;
		switch (a) {
			case 1:
				b = 1;
		}
		return b;
	}

	public int multipleCaseExpressions(int a) {
		int b = 0;
		switch (a) {
			case 1, 2:
				b = 1;
				break;
			default:
				break;
		}
		return b;
	}

	public void constructorCall() {
		new Object();
	}

	//All lines will be tested in this method
	public int simple(int a) {
		a = a + a / 2;
		return 10 * a;
	}

	public ControlFlowArithmetic() {
		int a = 1;
	}

	///////////////////////////////////////////////////////////////////////////////////////////


	public int testCase1(boolean armed, double inputs1, double inputs2, double THRESHOLD) {
		int count = 0;
		double value = inputs1;
		if (value < -THRESHOLD) {
			armed = true;
		} else if (armed & (value > THRESHOLD)) {
			++count;
			armed = false;
		}
		value = inputs2;
		if (value < -THRESHOLD) {
			armed = true;
		} else if (armed & (value > THRESHOLD)) {
			++count;
			armed = false;
		}
		return count;
	}

	double method1() {
		return 0;
	}

	double method2(double a) {
		return a;
	}

	boolean m3() {
		return true;
	}

	public void complex1(double phase, double source, double target, double baseIncrement, boolean starved, int i,
	                     double current, double[] outputs, double[] amplitudes, double[] rates) {
		if ((phase) >= 1.0) {
			while ((phase) >= 1.0) {
				source = target;
				phase -= 1.0;
				baseIncrement = method1();
			}
		} else if ((i == 0) && ((starved) || (!(m3())))) {
			source = target = current;
			phase = 0.0;
			baseIncrement = method1();
		}
		current = (((target) - (source)) * (phase)) + (source);
		outputs[i] = (current) * (amplitudes[i]);
		double phaseIncrement = (baseIncrement) * (rates[i]);
		phase += method2(phaseIncrement);
	}

}
