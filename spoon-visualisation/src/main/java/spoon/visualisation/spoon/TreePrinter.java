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

import io.github.interacto.command.library.OpenWebPage;
import java.net.URI;
import java.util.List;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import spoon.reflect.declaration.CtElement;

/**
 * The printer that prints the Spoon AST into a JavaFX tree view.
 */
public class TreePrinter extends SpoonElementVisitor {
	private final @NotNull TreeView<TextFlow> tree;
	private @Nullable TreeItem<TextFlow> currItem;
	/** The current depth level in the tree view */
	private int currLevel;

	/**
	 * @param tree The tree view to use to print the Spoon AST
	 * @param levelsToIgnore The number of tree levels to ignore before starting printing
	 */
	public TreePrinter(final @NotNull TreeView<TextFlow> tree, final int levelsToIgnore) {
		super(levelsToIgnore);
		this.tree = tree;
		this.tree.setRoot(null);
		this.tree.setShowRoot(false);
		currItem = null;
	}

	/**
	 * Create an initial text flow for naming the nodes of the Spoon AST.
	 * @param label The Spoon element to analyse.
	 * @return The created text flow that can be completed with other text elements.
	 */
	TextFlow createStdTextFlow(final TreeNodeLabel label) {
		// We want to be able to click on the interface name to open its JavaDoc
		final Hyperlink classLink = new Hyperlink(label.className);
		final TextFlow flow = new TextFlow(classLink);
		final String url = "http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/"
			+ label.fullName.replace('.', '/') + ".html";

		Tooltip.install(classLink, new Tooltip(url));

		// Clicking on the link opens the doc
		// 'new Thread' because otherwise the app freezes (run in the UI thread)
		classLink.setOnAction(evt -> new Thread(() -> {
			final OpenWebPage cmd = new OpenWebPage();
			cmd.setUri(URI.create(url));
			if(cmd.canDo()) {
				cmd.doIt();
			}
		}, "OPEN_SPOON_DOC_THREAD").start());

		label.implicit.ifPresent(txt -> flow.getChildren().add(new Text(txt)));
		label.role.ifPresent(txt -> flow.getChildren().add(new Text(txt)));
		label.additionals.ifPresent(txt -> flow.getChildren().add(new Text(txt)));

		return flow;
	}

	@Override
	public void visitElement(final CtElement elt, final int level, final @NotNull TreeNodeLabel label, final @NotNull List<Integer> lines) {
		// level > 1 because the root element must be created to be then masked as several real tree roots may exist
		// Example: three statements with the statement level.
		// level <= levelsToIgnore: depending on the analysis level, some root elements must be hidden
		if(level > 1 && level <= levelsToIgnore) {
			return;
		}

		final int startPosition = lines.isEmpty() ? -1 : lines.get(0);
		final int endPosition = lines.isEmpty() ? -1 : lines.get(1);
		final SpoonTreeItem item = new SpoonTreeItem(createStdTextFlow(label), startPosition, endPosition, elt);
		item.setExpanded(true);

		if(currItem == null) {
			tree.setRoot(item);
		}else {
			if(currLevel < level) {
				currItem.getChildren().add(item);
			}else {
				TreeItem<TextFlow> parent = currItem.getParent();

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
