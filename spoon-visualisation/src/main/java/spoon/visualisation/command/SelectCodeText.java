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
package spoon.visualisation.command;

import io.github.interacto.command.CommandImpl;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.jetbrains.annotations.NotNull;

/**
 * The command that selects the code elements corresponding to the selected tree item.
 */
public class SelectCodeText extends CommandImpl {
	private final int startPosition;
	private final int endPosition;
	private final @NotNull TextArea spoonCode;

	/**
	 * @param spoonCode The code view
	 * @param startPosition The starting position of the selection to perform in the code view (may be -1)
	 * @param endPosition The ending position of the selection to perform in the code view (may be -1)
	 */
	public SelectCodeText(final @NotNull TextArea spoonCode, final int startPosition, final int endPosition) {
		super();
		this.spoonCode = spoonCode;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
	}

	@Override
	protected void doCmdBody() {
		if(startPosition == -1) {
			Platform.runLater(() -> {
				spoonCode.deselect();
				spoonCode.requestFocus();
			});
		}else {
			Platform.runLater(() -> {
				spoonCode.selectRange(startPosition, endPosition + 1);
				spoonCode.requestFocus();
			});
		}
	}
}
