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

import javafx.scene.control.TreeView;
import javafx.scene.text.TextFlow;
import org.jetbrains.annotations.NotNull;
import spoon.visualisation.spoon.SpoonElementVisitor;
import spoon.visualisation.spoon.TreePrinter;

/**
 * The command that updates the tree view of the Spoon AST.
 */
public class UpdateSpoonTree extends SpoonTreeCmdBase {
	/** The tree widget that shows the Spoon tree. */
	private final @NotNull TreeView<TextFlow> spoonAST;

	/**
	 * @param spoonAST The tree view of the Spoon AST
	 * @param hideImplicit Hide implicit elements?
	 * @param code The code to analyse
	 * @param treeLevel The tree level analysis to use
	 */
	public UpdateSpoonTree(final @NotNull TreeView<TextFlow> spoonAST, final boolean hideImplicit, final @NotNull String code,
		final @NotNull TreeLevel treeLevel) {
		super(hideImplicit, code, treeLevel);
		this.spoonAST = spoonAST;
	}

	@Override
	SpoonElementVisitor createSpoonVisitor(final int levelsToIgnore) {
		return new TreePrinter(spoonAST, levelsToIgnore);
	}
}
