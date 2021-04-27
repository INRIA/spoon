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

import java.util.Objects;

/**
 * TriConsumer is a functional Consumer interface of three parameters.
 *
 * @param <T> Type of first parameter
 * @param <U> Type of second parameter
 * @param <V> Type of third parameter
 */
@FunctionalInterface
public interface TriConsumer<T, U, V> {
	void accept(T var1, U var2, V var3);

	default TriConsumer<T, U, V> andThen(TriConsumer<? super T, ? super U, ? super V> after) {
		Objects.requireNonNull(after);
		return (x, y, z) -> {
			this.accept(x, y, z);
			after.accept(x, y, z);
		};
	}
}
