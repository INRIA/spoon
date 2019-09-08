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
package spoon.visualisation.instrument;

import io.github.interacto.jfx.instrument.JfxInstrument;
import io.github.interacto.jfx.interaction.library.Click;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.stage.FileChooser;
import spoon.visualisation.command.SaveTreeText;
import spoon.visualisation.command.SelectCodeItem;
import spoon.visualisation.command.SelectCodeText;
import spoon.visualisation.command.TreeLevel;
import spoon.visualisation.command.UpdateSpoonTree;
import spoon.visualisation.spoon.SpoonTreeItem;

/**
 * The instrument that manages the main FXML view
 */
public class SpoonCodeInstrument extends JfxInstrument implements Initializable {
	@FXML private TextArea spoonCode;
	@FXML private TreeView<String> spoonAST;
	@FXML private CheckBox hideImplicit;
	@FXML private ComboBox<TreeLevel> treeLevel;
	@FXML private Button save;


	@Override
	public void initialize(final URL url, final ResourceBundle res) {
		setActivated(true);
		treeLevel.getItems().addAll(TreeLevel.values());
		treeLevel.getSelectionModel().select(TreeLevel.AUTO);
	}

	@Override
	protected void configureBindings() {
		// On text change, the spoon tree is rebuilt
		textInputBinder(i -> new UpdateSpoonTree(spoonAST, hideImplicit.isSelected(), "", treeLevel.getValue()))
			.on(spoonCode)
			.then((i, c) -> c.setCode(i.getWidget().getText()))
			.bind();

		final Supplier<UpdateSpoonTree> cmdsupplier =
			() -> new UpdateSpoonTree(spoonAST, hideImplicit.isSelected(), spoonCode.getText(), treeLevel.getValue());

		// Checking the checkbox hides or shows the implicit elements
		checkboxBinder(cmdsupplier)
			.on(hideImplicit)
			.bind();

		// Selecting an item of the combo box recomputes the spoon tree using the new analysis level
		comboboxBinder(cmdsupplier)
			.on(treeLevel)
			.bind();

		// Clicking on a tree item selects the corresponding Java code
		nodeBinder(new Click(), i -> {
				final SpoonTreeItem item = i.getSrcObject()
					.filter(o -> o.getParent() instanceof TreeCell)
					.map(o -> ((SpoonTreeItem) ((TreeCell<?>) o.getParent()).getTreeItem()))
					.orElseThrow();
				return new SelectCodeText(spoonCode, item.startPosition, item.endPosition);
			})
			.on(spoonAST)
			.when(i -> i.getSrcObject().filter(o -> o.getParent() instanceof TreeCell).isPresent())
			.bind();

		// Clicking in the text area (ie changing the caret position) selects (when relevant)
		// the corresponding item in the Spoon tree
		nodeBinder(new Click(), i -> new SelectCodeItem(spoonCode.getCaretPosition(), spoonAST))
			.on(spoonCode)
			.bind();

		// Clicking on the save button saves in a text file the text version of the Spoon tree
		buttonBinder(i -> new SaveTreeText(new FileChooser().showSaveDialog(null), hideImplicit.isSelected(),
				spoonCode.getText(), treeLevel.getValue()))
			.on(save)
			.bind();
	}
}
