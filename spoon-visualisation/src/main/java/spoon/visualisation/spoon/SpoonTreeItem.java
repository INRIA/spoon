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
package spoon.visualisation.spoon;

import javafx.scene.control.TreeItem;
import javafx.scene.text.TextFlow;
import spoon.reflect.declaration.CtElement;

/**
 * As JavaFX tree item cannot embed data, this tree item class embeds the code position
 * of the corresponding code element.
 */
public class SpoonTreeItem extends TreeItem<TextFlow> {
	public final int startPosition;
	public final int endPosition;
	public final CtElement elt;

	/**
	 * @param text The label of the tree item
	 * @param startPosition The starting position in the code of the corresponding code element
	 * @param endPosition The ending position in the code of the corresponding code element
	 */
	public SpoonTreeItem(final TextFlow text, final int startPosition, final int endPosition, final CtElement elt) {
		super(text);
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.elt = elt;
	}
}
