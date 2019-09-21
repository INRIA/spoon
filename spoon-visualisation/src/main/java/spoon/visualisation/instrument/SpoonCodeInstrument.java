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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.path.CtPath;
import spoon.support.DerivedProperty;
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
	@FXML private TreeView<TextFlow> spoonAST;
	@FXML private CheckBox hideImplicit;
	@FXML private ComboBox<TreeLevel> treeLevel;
	@FXML private Button save;
	@FXML private VBox propPanel;
	@FXML private ScrollPane scrollPaneProps;
	private FileChooser fileChooser;


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
		buttonBinder(i -> new SaveTreeText(getFileChooser().showSaveDialog(null), hideImplicit.isSelected(),
				spoonCode.getText(), treeLevel.getValue()))
			.on(save)
			.bind();

		spoonAST.getSelectionModel().selectedItemProperty().addListener((value, oldItem, newItem) -> {
			if(newItem == null) {
				cleanPropertiesPanel();
			}else {
				updatePropertiesPanel(((SpoonTreeItem) newItem).elt);
			}
		});
	}


	/**
	 * Lazy getter for a file chooser
	 * @return The file chooser of the component
	 */
	FileChooser getFileChooser() {
		if(fileChooser == null) {
			fileChooser = new FileChooser();
		}
		return fileChooser;
	}


	/**
	 * For testing purposes only.
	 * Allows to set the file chooser (a mock for example)
	 * @param fileChooser The file chooser to use.
	 */
	void setFileChooser(final FileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}


	/**
	 * Removes the displayed properties
	 */
	void cleanPropertiesPanel() {
		propPanel.getChildren().clear();
	}


	/**
	 * Updates the properties panel by computing the properties of the given Spoon element.
	 * @param elt The Spoon element to analyse. Cannot be null.
	 */
	void updatePropertiesPanel(final CtElement elt) {
		// Getting the properties and their value
		final List<Pair<Class<?>, Set<Pair<String, String>>>> props = getSpoonProperties(elt);
		// The idea is to have vertically a set of tables (and their title)
		propPanel.getChildren().clear();

		// Adding the CPath property
		final CtPath path = elt.getPath();
		if(path != null) {
			final TextField tf = new TextField(path.toString());
			tf.setEditable(false);
			propPanel.getChildren().add(new VBox(createTitleText("Path"), tf));
		}

		// A gap between each table
		propPanel.getChildren().addAll(
			props
				.stream()
				.map(pair -> {
					final VBox vBox = new VBox(createTitleText(pair.getKey().getName()), createTable(pair.getValue()));
					vBox.prefWidthProperty().bind(scrollPaneProps.widthProperty());
					return vBox;
				})
				.collect(Collectors.toList())
		);
	}


	Text createTitleText(final String txt) {
		final Text text = new Text(txt);
		text.setStyle("-fx-font-weight: bold");
		return text;
	}


	/**
	 * Creates the properties table of an interface given its properties values.
	 * @param props The properties values to used to create the table.
	 * @return The JFX table view.
	 */
	TableView<Pair<String, String>> createTable(final Set<Pair<String, String>> props) {
		final TableView<Pair<String, String>> table = new TableView<>();
		final TableColumn<Pair<String, String>, String> colName = new TableColumn<>("Name");
		final TableColumn<Pair<String, String>, String> colValue = new TableColumn<>("Value");

		// To fill the cells using the input pair of values (property name, value)
		colName.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getKey()));
		colValue.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().getValue()));
		table.getColumns().addAll(List.of(colName, colValue));
		// Balancing the width of each column
		table.getColumns().forEach(col -> col.prefWidthProperty().bind(table.widthProperty().divide(table.getColumns().size())));
		table.getItems().addAll(props);
		// Workaround to resize the table view according to its content
		table.setMinHeight(props.size() * 30 + 40);
		table.setMaxHeight(table.getMinHeight());
		table.setPrefHeight(table.getMinHeight());

		// Be able to copy the selected row
		final KeyCodeCombination keyCodeCopy = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY);
		table.setOnKeyPressed(event -> {
			if (keyCodeCopy.match(event) && table.getSelectionModel().getSelectedItem() != null) {
				final Pair<String, String> item = table.getSelectionModel().getSelectedItem();
				final ClipboardContent clipboardContent = new ClipboardContent();
				clipboardContent.putString(item.getKey() + ": " + item.getValue());
				Clipboard.getSystemClipboard().setContent(clipboardContent);
			}
		});

		return table;
	}


	/**
	 * Gets the Spoon properties and their current value of the given element.
	 * A Spoon properties is a getter (get..., is...).
	 * Note that Spoon has a specific annotation (PropertyGetter, also DerivedProperty) for that we do not use here.
	 * @param elt The Spoon element to analyse
	 * @return A list of pairs: each pair refers to:
	 * 1/ an interface that the Spoon element implements
	 * 2/ a set of pairs where each pair refers to a property (its name) and its value
	 */
	List<Pair<Class<?>, Set<Pair<String, String>>>> getSpoonProperties(final CtElement elt) {
		// Getting all the Spoon interfaces that 'elt' implements
		return getAllSpoonInterfaces(elt.getClass())
			// Transforming each interface as a pair. The left element is the name of the interface
			// The right element is a set of properties and their value.
			.map(inter -> new Pair<Class<?>, Set<Pair<String, String>>>(inter, Arrays.stream(inter.getDeclaredMethods())
				// Ignoring the methods that are static, with parameters, that are not getters
				.filter(m ->
					(m.isAnnotationPresent(PropertyGetter.class) || m.isAnnotationPresent(DerivedProperty.class)) &&
						m.getParameterCount() == 0)
				// Transforming each method as a pair of: the method name (we call property), and its current value in 'elt'
				.map(m -> {
					try {
						return new Pair<>(m.getName() + "(): " + prettyPrintType(m.getGenericReturnType()), String.valueOf(m.invoke(elt)));
					}catch(final IllegalAccessException | InvocationTargetException | NullPointerException ex) {
						return null;
					}
				})
				.filter(entry -> entry != null)
				.collect(Collectors.toSet()))
			)
			// Ignoring the interfaces with no property
			.filter(pair -> !pair.getValue().isEmpty())
			.collect(Collectors.toList());
	}


	/**
	 * Gets a short pretty print of the given type by considering its generics
	 * @param type The type to analyse
	 * @return A string corresponding to the name of the type plus potential generics
	 */
	String prettyPrintType(final Type type) {
		if(type instanceof Class) {
			return ((Class<?>) type).getSimpleName();
		}
		if(type instanceof ParameterizedType) {
			final ParameterizedType paramType = (ParameterizedType) type;
			return getUnqualifiedClassName(paramType.getRawType().getTypeName()) +
				Arrays
					.stream(paramType.getActualTypeArguments())
					.map(arg -> prettyPrintType(arg))
					.collect(Collectors.joining(", ", "<", ">"));
		}
		return type.getTypeName();
	}


	/**
	 * Transforms a qualified name as an unqualified name.
	 * @param qname The qualified name to transform (cannot be null or empty)
	 * @return The computed unqualified name
	 */
	String getUnqualifiedClassName(final String qname) {
		final String[] parts = qname.split("\\.");
		return parts[parts.length - 1];
	}


	/**
	 * A helper method for retrieving all the interfaces of the Spoon API used by the given class.
	 * @param cl The class to analyse.
	 * @return A stream of all the retrieved interfaces
	 */
	Stream<Class<?>> getAllSpoonInterfaces(final Class<?> cl) {
		// Getting all the direct interfaces plus the undirect ones
		return Stream.concat(
			getDirectSpoonInterfaces(cl),
			getDirectSpoonInterfaces(cl)
				.map(inter -> getAllSpoonInterfaces(inter))
				.flatMap(st -> st)
		).distinct();
	}


	/**
	 * A helper method for retrieving the interfaces of the Spoon API that the given class directly implements.
	 * @param cl The class to analyse.
	 * @return A stream of all the retrieved interfaces
	 */
	Stream<Class<?>> getDirectSpoonInterfaces(final Class<?> cl) {
		return Arrays
			.stream(cl.getInterfaces())
			.filter(inter -> inter.getPackageName().startsWith("spoon."));
	}
}
