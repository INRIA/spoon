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

import java.util.ArrayList;

/**
 * Created by marcel on 23/02/14.
 * <p/>
 * This class includes all the complex cases we have found in which the algorithm failed.
 */
public class ComplexFlow {

	private int[] ip;
	private double[] w;


	private int p = 0;
	private MyCF<Double> e;
	private MyCF<Double> s;
	private MyCF<Double> v;
	private MyCF<Double> u;
	private int cc;
	private int rc;

	public static abstract class MyCF<T> extends ArrayList<T> {
		public abstract void set(int index, int index2, T w);

		public abstract T get(int v, int w);

		public abstract T unsafeGet(int index);
	}

	/**
	 * Status: Not solved
	 */
	private void complexFlow1() {
		int pp = p - 1;
		int iter = 0;
		double eps = Math.pow(2.0, -52.0);
		double tiny = Math.pow(2.0, -966.0);

		while (p > 0) {

			int k, kase;

			for (k = p - 2; k >= -1; k--) {
				if (k == -1) break;

				if (Math.abs(e.get(k)) <= tiny
						+ eps
						* (Math.abs(s.get(k)) + Math
						.abs(s.get(k + 1)))) {
					e.set(k, 0.0);
					break;
				}
			}

			if (k == p - 2) {

				kase = 4;

			} else {

				int ks;

				for (ks = p - 1; ks >= k; ks--) {

					if (ks == k) break;

					double t = (ks != p ? Math.abs(e.get(ks)) : 0.)
							+ (ks != k + 1 ? Math.abs(e.get(ks - 1)) : 0.);

					if (Math.abs(s.get(ks)) <= tiny + eps * t) {
						s.set(ks, 0.0);
						break;
					}
				}

				if (ks == k) {
					kase = 3;
				} else if (ks == p - 1) {
					kase = 1;
				} else {
					kase = 2;
					k = ks;
				}
			}

			k++;

			switch (kase) {

				case 1: {
					double f = e.get(p - 2);
					e.set(p - 2, 0.0);

					for (int j = p - 2; j >= k; j--) {

						double sj = s.get(j);
						double t = Math.hypot(sj, f);
						double cs = sj / t;
						double sn = f / t;

						s.set(j, j, t);

						if (j != k) {
							f = -sn * e.get(j - 1);
							e.set(j - 1, cs * e.get(j - 1));
						}

						for (int i = 0; i < cc; i++) {
							t = cs * v.get(i, j) + sn * v.get(i, p - 1);
							v.set(i, p - 1,
									-sn * v.get(i, j) + cs * v.get(i, p - 1));
							v.set(i, j, t);
						}
					}
				}
				break;

				case 2: {
					double f = e.get(k - 1);
					e.set(k - 1, 0.0);

					for (int j = k; j < p; j++) {

						double sj = s.get(j);
						double t = Math.hypot(sj, f);
						double cs = sj / t;
						double sn = f / t;

						s.set(j, j, t);
						f = -sn * e.get(j);
						e.set(j, cs * e.get(j));

						for (int i = 0; i < rc; i++) {
							t = cs * u.get(i, j) + sn * u.get(i, k - 1);
							u.set(i, k - 1,
									-sn * u.get(i, j) + cs * u.get(i, k - 1));
							u.set(i, j, t);
						}
					}
				}
				break;

				case 3: {

					double scale = Math
							.max(Math.max(Math.max(
									Math.max(Math.abs(s.get(p - 1)),
											Math.abs(s.get(p - 2))),
									Math.abs(e.get(p - 2))), Math.abs(s.get(k))),
									Math.abs(e.get(k)));

					double sp = s.get(p - 1) / scale;
					double spm1 = s.get(p - 2) / scale;
					double epm1 = e.get(p - 2) / scale;
					double sk = s.get(k) / scale;
					double ek = e.get(k) / scale;
					double b = ((spm1 + sp) * (spm1 - sp) + epm1 * epm1) / 2.0;
					double c = (sp * epm1) * (sp * epm1);
					double shift = 0.0;

					if ((b != 0.0) | (c != 0.0)) {
						shift = Math.sqrt(b * b + c);
						if (b < 0.0) {
							shift = -shift;
						}
						shift = c / (b + shift);
					}

					double f = (sk + sp) * (sk - sp) + shift;
					double g = sk * ek;

					for (int j = k; j < p - 1; j++) {
						double t = Math.hypot(f, g);
						double cs = f / t;
						double sn = g / t;

						if (j != k) {
							e.set(j - 1, t);
						}

						double sj = s.get(j);

						f = cs * sj + sn * e.get(j);
						e.set(j, cs * e.get(j) - sn * sj);
						g = sn * s.get(j + 1);
						s.set(j + 1, cs * s.get(j + 1));

						for (int i = 0; i < cc; i++) {
							t = cs * v.get(i, j) + sn * v.get(i, j + 1);
							v.set(i, j + 1,
									-sn * v.get(i, j) + cs * v.get(i, j + 1));
							v.set(i, j, t);
						}

						t = Math.hypot(f, g);
						cs = f / t;
						sn = g / t;
						s.set(j, t);
						f = cs * e.get(j) + sn * s.get(j + 1);
						s.set(j + 1,
								-sn * e.get(j) + cs * s.get(j + 1));
						g = sn * e.get(j + 1);
						e.set(j + 1, e.get(j + 1) * (cs));

						if (j < rc - 1) {
							for (int i = 0; i < rc; i++) {
								t = cs * u.get(i, j) + sn * u.get(i, j + 1);
								u.set(i, j + 1,
										-sn * u.get(i, j) + cs * u.get(i, j + 1));
								u.set(i, j, t);
							}
						}
					}

					e.set(p - 2, f);
					iter = iter + 1;
				}
				break;

				case 4: {
					double skk = s.get(k);
					if (skk <= 0.0) {
						s.set(k, -skk);
						for (int i = 0; i <= pp; i++) {
							v.set(i, k, -v.get(i, k));
						}
					}

					while (k < pp) {

						if (s.get(k) >= s.get(k + 1)) break;


						double t = s.get(k);
						s.set(k, s.get(k + 1));
						s.set(k + 1, t);

						if (k < cc - 1) {
							v.set(k, (double) k + 1);
						}

						if (k < rc - 1) {
							u.set(k, (double) k + 1);
						}

						k++;
					}

					iter = 0;
					p--;
				}
				break;
			}
		}
	}


	/**
	 * Status: Solved.
	 * @param nw
	 */
	private void makewt(int nw) {
		int j, nwh, nw0, nw1;
		double delta, wn4r, wk1r, wk1i, wk3r, wk3i;
		double delta2, deltaj, deltaj3;

		ip[0] = nw;
		ip[1] = 1;
		if (nw > 2) {
			nwh = nw >> 1;
			delta = 0.785398163397448278999490867136046290 / nwh;
			delta2 = delta * 2;
			wn4r = Math.cos(delta * nwh);

			w[0] = 1;
			w[1] = wn4r;
			if (nwh == 4) {
				w[2] = Math.cos(delta2);
				w[3] = Math.sin(delta2);
			} else if (nwh > 4) {
				makeipt(nw);
				w[2] = 0.5 / Math.cos(delta2);
				w[3] = 0.5 / Math.cos(delta * 6);
				for (j = 4; j < nwh; j += 4) {
					deltaj = delta * j;
					deltaj3 = 3 * deltaj;
					w[j] = Math.cos(deltaj);
					w[j + 1] = Math.sin(deltaj);
					w[j + 2] = Math.cos(deltaj3);
					w[j + 3] = -Math.sin(deltaj3);
				}
			}
			nw0 = 0;
			while (nwh > 2) {
				nw1 = nw0 + nwh;
				nwh >>= 1;
				w[nw1] = 1;
				w[nw1 + 1] = wn4r;
				if (nwh == 4) {
					wk1r = w[nw0 + 4];
					wk1i = w[nw0 + 5];
					w[nw1 + 2] = wk1r;
					w[nw1 + 3] = wk1i;
				} else if (nwh > 4) {
					wk1r = w[nw0 + 4];
					wk3r = w[nw0 + 6];
					w[nw1 + 2] = 0.5 / wk1r;
					w[nw1 + 3] = 0.5 / wk3r;
					for (j = 4; j < nwh; j += 4) {
						int idx1 = nw0 + 2 * j;
						int idx2 = nw1 + j;
						wk1r = w[idx1];
						wk1i = w[idx1 + 1];
						wk3r = w[idx1 + 2];
						wk3i = w[idx1 + 3];
						w[idx2] = wk1r;
						w[idx2 + 1] = wk1i;
						w[idx2 + 2] = wk3r;
						w[idx2 + 3] = wk3i;
					}
				}
				nw0 = nw1;
			}
		}
	}

	public boolean armed = false;

	private double simpleflow(double[] inputs, double threshold) {
		int count = inputs.length;
		double notInitialized;
		double value = inputs[0];
		if (value < -threshold) {
			armed = true;
			notInitialized = inputs[2] + count;
			return value * notInitialized;
		} else if (armed && (value > threshold)) {
			++count;
			armed = false;
		}
		return value * count;
	}

	//Switch complex
	int n;

	public void realForwardFull(final double[] a, final int offa, int plan) {
		final int twon = 2 * (n);
		switch (plan) {
			case 1:
				realForwardFull(a, offa, plan);
			{
				int idx1;
				int idx2;
				for (int k = 0; k < ((n) / 2); k++) {
					idx1 = 2 * k;
					idx2 = offa + ((twon - idx1) % twon);
					a[idx2] = a[(offa + idx1)];
					a[(idx2 + 1)] = -(a[((offa + idx1) + 1)]);
				}
			}
			a[(offa + (n))] = -(a[(offa + 1)]);
			a[(offa + 1)] = 0;
			break;
			case 2:
				realForwardFull(a, offa, plan);
				int m;
				if (((n) % 2) == 0) {
					m = (n) / 2;
				} else {
					m = ((n) + 1) / 2;
				}
				for (int k = 1; k < m; k++) {
					int idx1 = (offa + twon) - (2 * k);
					int idx2 = offa + (2 * k);
					a[(idx1 + 1)] = -(a[idx2]);
					a[idx1] = a[(idx2 - 1)];
				}
				for (int k = 1; k < (n); k++) {
					int idx = (offa + (n)) - k;
					double tmp = a[(idx + 1)];
					a[(idx + 1)] = a[idx];
					a[idx] = tmp;
				}
				a[(offa + 1)] = 0;
				break;
			case 3:
				realForwardFull(a, offa, -1);
				break;
		}
	}

	private void makeipt(int nw) {

	}

	public static class Index54 {
		public double data[];
	}

	public int twoLoops(ArrayList<Index54> e) {
		int result = 0;
		for (int i = 0; i < e.size(); i++) {
			result += e.get(i).data[i] * 2;
		}

		result = 0;
		for (int i = 0; i < e.size(); i++) {
			result += e.get(i).data[i] * 4;
		}

		return result;
	}

	public static boolean isPositiveSemiDefinite(ArrayList<Index54> e) {
		ArrayList<Index54> eigenValues = e;
		for (Index54 v : eigenValues) {
			if (v.data[0] < 0) return false;
		}
		return true;
	}
}
