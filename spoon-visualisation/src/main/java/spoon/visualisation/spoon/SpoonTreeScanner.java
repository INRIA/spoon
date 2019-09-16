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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.CtScanner;

/**
 * Scans Java code using a Spoon scanner to extract Spoon tree information.
 */
public class SpoonTreeScanner extends CtScanner {
	/** If true, the scanner will ignore implicit elements */
	private final boolean hideImplicit;
	/** The printer that is in charge of showing the Spoon tree. */
	private final @NotNull SpoonElementVisitor printer;
	/** The current deepness level. */
	private int level;
	/** the role of the current element. May be null. */
	private @Nullable CtRole currRole;


	/**
	 * @param printer The printer that is in charge of showing the Spoon tree.
	 * The first argument (integer), is the deepness of the current element in the tree structure.
	 * The second argument (string), is the label of the current element to display.
	 * The third argument (list) contains the start and end line of the element. May be empty.
	 * @param hideImplicit If true, the scanner will ignore implicit elements
	 */
	public SpoonTreeScanner(final @NotNull SpoonElementVisitor printer, final boolean hideImplicit) {
		super();
		this.hideImplicit = hideImplicit;
		this.printer = printer;
		level = 0;
	}


	@Override
	public void scan(final CtRole role, final CtElement element) {
		// Filtering out implicit elements;
		if(element != null && hideImplicit && element.isImplicit()) {
			return;
		}

		currRole = role;
		super.scan(role, element);
		currRole = null;
	}


	@Override
	protected void enter(final CtElement elt) {
		level++;

		final TextFlow flow = createStdTextFlow(elt);
		final SourcePosition pos = elt.getPosition();
		final List<Integer> lines = pos.isValidPosition() ? List.of(pos.getSourceStart(), pos.getSourceEnd()) : List.of();

		if(elt instanceof CtType<?>) {
			flow.getChildren().add(new Text(": " + ((CtType<?>) elt).getSimpleName()));
			printer.accept(level, flow, lines);
			return;
		}

		if(elt instanceof CtNamedElement) {
			flow.getChildren().add(new Text(": " + ((CtNamedElement) elt).getSimpleName()));
			printer.accept(level, flow, lines);
			return;
		}

		if(elt instanceof CtReference) {
			flow.getChildren().add(new Text(": " + ((CtReference) elt).getSimpleName()));
			printer.accept(level, flow, lines);
			return;
		}

		if(elt instanceof CtVariableAccess<?>) {
			final CtVariableAccess<?> varaccess = (CtVariableAccess<?>) elt;
			final String txt = ": " + ((varaccess.getVariable() != null) ? varaccess.getVariable().getSimpleName() : "(null)");
			flow.getChildren().add(new Text(txt));
			printer.accept(level, flow, lines);
			return;
		}

		if(elt instanceof CtTypeAccess<?>) {
			final CtTypeAccess<?> typeaccess = (CtTypeAccess<?>) elt;
			final String txt = ": " + ((typeaccess.getAccessedType() != null) ? typeaccess.getAccessedType().getSimpleName() : "(null)");
			flow.getChildren().add(new Text(txt));
			printer.accept(level, flow, lines);
			return;
		}

		if(elt instanceof CtLiteral<?>) {
			flow.getChildren().add(new Text(": " + ((CtLiteral<?>) elt).getValue()));
			printer.accept(level, flow, lines);
			return;
		}

		if(elt instanceof CtAbstractInvocation<?>) {
			final CtAbstractInvocation<?> invoc = (CtAbstractInvocation<?>) elt;
			final String txt = ": " + ((invoc.getExecutable() != null) ? invoc.getExecutable().getSimpleName() : "(null)");
			flow.getChildren().add(new Text(txt));
			printer.accept(level, flow, lines);
			return;
		}

		if(elt instanceof CtAnnotation<?>) {
			final CtAnnotation<?> annot = (CtAnnotation<?>) elt;
			final String txt = ": " + ((annot.getAnnotationType() != null) ? annot.getAnnotationType().getSimpleName() : "(null)");
			flow.getChildren().add(new Text(txt));
			printer.accept(level, flow, lines);
			return;
		}

		printer.accept(level, flow, lines);
	}


	@Override
	protected void exit(final CtElement e) {
		level--;
		super.exit(e);
	}


	/**
	 * Create an initial text flow for naming the nodes of the Spoon AST.
	 * @param elt The Spoon element to analyse.
	 * @return The created text flow that can be completed with other text elements.
	 */
	TextFlow createStdTextFlow(final CtElement elt) {
		final String simpleName = elt.getClass().getSimpleName();
		// We assume that the Spoon API follows this rule:
		// the implementation class name is the interface name plus 'Impl'
		// We want to display the main interface corresponding to the given class.
		// So looking for this interface. If not found, the current class is used.
		final Class<?> cl = Arrays
			.stream(elt.getClass().getInterfaces())
			.filter(interf -> simpleName.equals(interf.getSimpleName() + "Impl"))
			.findFirst()
			.orElse(elt.getClass());
		// We want to be able to click on the interface name to open its JavaDoc
		final Hyperlink classLink = new Hyperlink(cl.getSimpleName());
		final TextFlow flow = new TextFlow(classLink);
		final String url = "http://spoon.gforge.inria.fr/mvnsites/spoon-core/apidocs/" + cl.getName().replace('.', '/') + ".html";
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

		if(elt.isImplicit()) {
			flow.getChildren().add(new Text("(implicit)"));
		}

		if(currRole != null) {
			flow.getChildren().add(new Text("(role: " + currRole + ")"));
		}

		// Adding a link to show the properties and their value.
		final Hyperlink attrsLink = new Hyperlink("(properties)");
		attrsLink.setOnAction(ignored -> createPropertiesStage(elt).show());
		flow.getChildren().add(attrsLink);

		return flow;
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
	 * Creates a stage (a window) that shows the properties and their value of a given Spoon element.
	 * @param elt The Spoon element to analyse.
	 * @return The created stage.
	 */
	Stage createPropertiesStage(final CtElement elt) {
		// Getting the properties and their value
		final List<Pair<Class<?>, Set<Pair<String, String>>>> props = getSpoonProperties(elt);
		// The idea is to have vertically a set of tables (and their title)
		final VBox layout = new VBox();
		final ScrollPane scroll = new ScrollPane(layout);
		layout.prefWidthProperty().bind(scroll.prefWidthProperty());

		// A gap between each table
		layout.setSpacing(20);
		layout.getChildren().addAll(
			props
				.stream()
				.map(pair -> {
					// Creating a title
					final Text text = new Text(pair.getKey().getName());
					text.setStyle("-fx-font-weight: bold");
					// Creating the table
					return new VBox(text, createTable(pair.getValue()));
				})
				.collect(Collectors.toList())
		);


		final Stage stage = new Stage();
		final Scene scene = new Scene(scroll, 800, 700);
		scroll.prefWidthProperty().bind(scene.widthProperty());
		stage.setScene(scene);
		stage.setTitle("Properties values");
		return stage;
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
					!Modifier.isStatic(m.getModifiers()) &&
					m.getParameterCount() == 0 &&
					(m.getName().startsWith("get") || m.getName().startsWith("is")))
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
				.map(arg -> getUnqualifiedClassName(arg.getTypeName()))
				.collect(Collectors.joining(", ", "<", ">"));
		}
		return type.getTypeName();
	}


	/**
	 * Transforms a qualified name as an unqualified name.
	 * @param qname The qualified name to transform
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
