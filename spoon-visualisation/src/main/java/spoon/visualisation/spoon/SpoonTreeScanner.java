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
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
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
	private TextFlow createStdTextFlow(final CtElement elt) {
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
	 * Creates a stage (a window) that shows the properties and their value of a given Spoon element.
	 * @param elt The Spoon element to analyse.
	 * @return The created stage.
	 */
	private Stage createPropertiesStage(final CtElement elt) {
		final TableView<Tuple4> table = new TableView<>();
		final TableColumn<Tuple4, String> colName = new TableColumn<>("Name");
		final TableColumn<Tuple4, String> declType = new TableColumn<>("Declared Type");
		final TableColumn<Tuple4, String> realType = new TableColumn<>("Real Type");
		final TableColumn<Tuple4, String> colValue = new TableColumn<>("Value");

		colName.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().v1));
		declType.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().v2));
		realType.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().v3));
		colValue.setCellValueFactory(p -> new ReadOnlyObjectWrapper<>(p.getValue().v4));
		table.getColumns().addAll(List.of(colName, declType, realType, colValue));
		table.getColumns().forEach(col -> col.prefWidthProperty().bind(table.widthProperty().divide(table.getColumns().size())));
		table.getItems().addAll(getSpoonAttributes(elt));

		final Scene scene = new Scene(table);
		final Stage stage = new Stage();
		stage.setScene(scene);
		table.setPrefSize(1000, 800);
		stage.setTitle("Properties values");
		return stage;
	}


	private Set<Tuple4> getSpoonAttributes(final CtElement elt) {
		return getAllSpoonInterfaces(elt.getClass())
			.map(inter -> Arrays.stream(inter.getMethods()))
			.flatMap(s -> s)
			.filter(m -> !Modifier.isStatic(m.getModifiers()) && m.getParameterCount() == 0 &&
				(m.getName().startsWith("get") || m.getName().startsWith("is")))
			.map(m -> {
				try {
					final Object obj = m.invoke(elt);
					final String objTypeName = obj == null ? "null" : obj.getClass().getSimpleName();
					return new Tuple4(m.getName() + "()", m.getReturnType().getSimpleName(), objTypeName, String.valueOf(obj));
				}catch(final IllegalAccessException | InvocationTargetException | NullPointerException ex) {
					return null;
				}
			})
			.filter(entry -> entry != null)
			.collect(Collectors.toSet());
	}


	/**
	 * A helper method for retrieving all the interfaces of the Spoon API used by the given class.
	 * @param cl The class to analyse.
	 * @return A stream of all the retrieved interfaces
	 */
	private Stream<Class<?>> getAllSpoonInterfaces(final Class<?> cl) {
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
	private Stream<Class<?>> getDirectSpoonInterfaces(final Class<?> cl) {
		return Arrays
			.stream(cl.getInterfaces())
			.filter(inter -> inter.getPackageName().startsWith("spoon."));
	}
}
