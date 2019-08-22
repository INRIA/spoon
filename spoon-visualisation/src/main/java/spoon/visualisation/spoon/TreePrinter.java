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

import java.util.List;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The printer that prints the Spoon AST into a JavaFX tree view.
 */
public class TreePrinter extends SpoonElementVisitor {
	private final @NotNull TreeView<String> tree;
	private @Nullable TreeItem<String> currItem;
	/** The current depth level in the tree view */
	private int currLevel;

	/**
	 * @param tree The tree view to use to print the Spoon AST
	 * @param levelsToIgnore The number of tree levels to ignore before starting printing
	 */
	public TreePrinter(final @NotNull TreeView<String> tree, final int levelsToIgnore) {
		super(levelsToIgnore);
		this.tree = tree;
		this.tree.setRoot(null);
		this.tree.setShowRoot(false);
		currItem = null;
	}

	@Override
	public void accept(final int level, final @NotNull String label, final @NotNull List<Integer> lines) {
		// level > 1 because the root element must be created to be then masked as several real tree roots may exist
		// Example: three statements with the statement level.
		// level <= levelsToIgnore: depending on the analysis level, some root elements must be hidden
		if(level > 1 && level <= levelsToIgnore) {
			return;
		}

		final int startPosition = lines.isEmpty() ? -1 : lines.get(0);
		final int endPosition = lines.isEmpty() ? -1 : lines.get(1);
		final SpoonTreeItem item = new SpoonTreeItem(label, startPosition, endPosition);
		item.setExpanded(true);

		if(currItem == null) {
			tree.setRoot(item);
		}else {
			if(currLevel < level) {
				currItem.getChildren().add(item);
			}else {
				TreeItem<String> parent = currItem.getParent();

				while(currLevel > level) {
					currLevel--;
					parent = parent.getParent();
				}

				if(parent != null) {
					parent.getChildren().add(item);
				}
			}
		}

		currLevel = level;
		currItem = item;
	}
}
