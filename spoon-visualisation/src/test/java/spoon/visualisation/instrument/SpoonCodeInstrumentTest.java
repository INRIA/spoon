package spoon.visualisation.instrument;

import io.github.interacto.command.CmdHandler;
import io.github.interacto.command.CommandsRegistry;
import io.github.interacto.command.library.OpenWebPage;
import io.github.interacto.fsm.TimeoutTransition;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.assertj.core.api.AssertionsForClassTypes;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;
import spoon.processing.FactoryAccessor;
import spoon.reflect.annotations.PropertyGetter;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtLoop;
import spoon.reflect.code.CtStatement;
import spoon.reflect.cu.SourcePositionHolder;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.path.CtPath;
import spoon.reflect.visitor.CtVisitable;
import spoon.reflect.visitor.chain.CtQueryable;
import spoon.visualisation.command.TreeLevel;

import static org.assertj.core.api.Assertions.assertThat;
import static spoon.reflect.path.CtRole.ANNOTATION;

@ExtendWith(ApplicationExtension.class)
class SpoonCodeInstrumentTest {
	SpoonCodeInstrument spoonCodeInstrument;
	TreeView<TextFlow> spoonAST;
	TextArea spoonCode;
	ComboBox<TreeLevel> treeLevel;
	CheckBox hideImplicit;

	static void waitForThread(final String threadName) {
		Thread.getAllStackTraces().keySet()
			.stream()
			.filter(thread -> thread.getName().startsWith(threadName))
			.forEach(thread -> {
				try {
					thread.join();
				}catch(final InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
			});
		WaitForAsyncUtils.waitForFxEvents();
	}

	static void waitForTimeoutTransitions() {
		waitForThread(TimeoutTransition.TIMEOUT_THREAD_NAME_BASE);
	}


	@Start
	void start(final Stage stage) throws IOException {
		final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UI.fxml"));
		loader.load();
		spoonCodeInstrument = loader.getController();
		stage.setScene(new Scene(loader.getRoot()));
		stage.show();
	}

	@BeforeEach
	void setUp(final FxRobot robot) {
		CommandsRegistry.INSTANCE.clear();
		CommandsRegistry.INSTANCE.removeAllHandlers();
		spoonAST = robot.lookup("#spoonAST").query();
		spoonCode = robot.lookup("#spoonCode").query();
		treeLevel = robot.lookup("#treeLevel").query();
		hideImplicit = robot.lookup("#hideImplicit").query();
	}

	@Test
	void testTypeCodeCreatesTheAST(final FxRobot robot) {
		robot.clickOn(spoonCode).write("class Foo { }");
		waitForTimeoutTransitions();
		final ObservableList<TreeItem<TextFlow>> ast = spoonAST.getRoot().getChildren();

		AssertionsForClassTypes.assertThat(ast.size()).isEqualTo(1);
		AssertionsForClassTypes.assertThat(ast.get(0).getValue()
			.getChildren()
			.stream()
			.anyMatch(ch -> ch instanceof Text && ((Text) ch).getText().contains("Foo"))
		).isTrue();
	}

	@Test
	void testClassElementLevel(final FxRobot robot) {
		robot.clickOn(spoonCode).write("int i;");
		waitForTimeoutTransitions();
		robot.clickOn(treeLevel).type(KeyCode.DOWN).type(KeyCode.ENTER);
		WaitForAsyncUtils.waitForFxEvents();
		final ObservableList<TreeItem<TextFlow>> ast = spoonAST.getRoot().getChildren();

		AssertionsForClassTypes.assertThat(ast.size()).isEqualTo(1);
		AssertionsForClassTypes.assertThat(ast.get(0).getValue()
			.getChildren()
			.stream()
			.anyMatch(ch -> ch instanceof Hyperlink && ((Hyperlink) ch).getText().equals("CtField"))
		).isTrue();
	}

	@Test
	void testStatementLevel(final FxRobot robot) {
		robot.clickOn(spoonCode).write("int i;");
		waitForTimeoutTransitions();
		robot.clickOn(treeLevel).type(KeyCode.DOWN).type(KeyCode.DOWN).type(KeyCode.ENTER);
		WaitForAsyncUtils.waitForFxEvents();
		final ObservableList<TreeItem<TextFlow>> ast = spoonAST.getRoot().getChildren();

		AssertionsForClassTypes.assertThat(ast.size()).isEqualTo(1);
		AssertionsForClassTypes.assertThat(ast.get(0).getValue()
			.getChildren()
			.stream()
			.anyMatch(ch -> ch instanceof Hyperlink && ((Hyperlink) ch).getText().equals("CtLocalVariable"))
		).isTrue();
	}

	@Test
	void testExpLevel(final FxRobot robot) {
		robot.clickOn(spoonCode).write("1 < 2");
		waitForTimeoutTransitions();
		robot.clickOn(treeLevel).type(KeyCode.DOWN).type(KeyCode.DOWN).type(KeyCode.DOWN).type(KeyCode.ENTER);
		WaitForAsyncUtils.waitForFxEvents();
		final ObservableList<TreeItem<TextFlow>> ast = spoonAST.getRoot().getChildren();

		AssertionsForClassTypes.assertThat(ast.size()).isEqualTo(1);
		AssertionsForClassTypes.assertThat(ast.get(0).getValue()
			.getChildren()
			.stream()
			.anyMatch(ch -> ch instanceof Hyperlink && ((Hyperlink) ch).getText().equals("CtBinaryOperator"))
		).isTrue();
	}

	@Test
	void testShowImplicit(final FxRobot robot) {
		robot.clickOn(hideImplicit);
		robot.clickOn(spoonCode).write("class Foo { }");
		waitForTimeoutTransitions();
		final TreeItem<TextFlow> item = spoonAST.getTreeItem(1);

		AssertionsForClassTypes.assertThat(item.getValue()
			.getChildren()
			.stream()
			.anyMatch(ch -> ch instanceof Text && ((Text) ch).getText().contains("(implicit)"))
		).isTrue();
	}

	@Test
	void testHideImplicit(final FxRobot robot) {
		robot.clickOn(spoonCode).write("class Foo { }");
		waitForTimeoutTransitions();
		final TreeItem<TextFlow> item = spoonAST.getTreeItem(1);

		AssertionsForClassTypes.assertThat(item).isNull();
	}

	@Test
	void testNumberOfHyperlinks(final FxRobot robot) {
		robot.clickOn(spoonCode).write("class Foo { } class Bar {}");
		waitForTimeoutTransitions();
		final Set<Node> hyperlinks = robot.lookup(CoreMatchers.instanceOf(Hyperlink.class)).queryAll();

		AssertionsForClassTypes.assertThat(hyperlinks.size()).isEqualTo(2);
	}

	@Disabled("Does not work on headless server")
	@Test
	void testClickHyperlink(final FxRobot robot) {
		robot.clickOn(spoonCode).write("class Foo { }");
		waitForTimeoutTransitions();
		final CmdHandler handler = Mockito.mock(CmdHandler.class);
		CommandsRegistry.INSTANCE.addHandler(handler);
		final Set<Node> hyperlinks = robot.lookup(CoreMatchers.instanceOf(Hyperlink.class)).queryAll();
		robot.clickOn(hyperlinks.iterator().next());
		WaitForAsyncUtils.waitForFxEvents();
		waitForThread("OPEN_SPOON_DOC_THREAD");

		Mockito.verify(handler, Mockito.times(1)).onCmdExecuted(Mockito.any(OpenWebPage.class));
	}


	@Disabled("Does not work on headless servers")
	@Test
	void testSaveText(final FxRobot robot, @TempDir final Path tempDir) throws IOException {
		final Path path = tempDir.resolve("test.txt");
		final File file = path.toFile();
		final FileChooser mockChooser = Mockito.mock(FileChooser.class);

		Mockito.when(mockChooser.showSaveDialog(null)).thenReturn(file);
		spoonCodeInstrument.setFileChooser(mockChooser);

		robot.clickOn(spoonCode).write("class Foo { int i; }");
		waitForTimeoutTransitions();

		final Button save = robot.lookup("#save").query();
		robot.clickOn(save);
		WaitForAsyncUtils.waitForFxEvents();

		final List<String> lines = Files.readAllLines(path);

		assertThat(lines.size()).isEqualTo(3);
		assertThat(lines.get(0)).contains("CtClass");
		assertThat(lines).noneMatch(l -> l.contains("TextFlow@"));
	}


	@Test
	void getDirectSpoonInterfacesOfClass() {
		final List<Class<?>> inter = spoonCodeInstrument.getDirectSpoonInterfaces(CtClass.class).collect(Collectors.toList());
		assertThat(inter).containsExactlyInAnyOrder(CtType.class, CtStatement.class);
	}

	@Test
	void getDirectSpoonInterfacesOfFor() {
		final List<Class<?>> inter = spoonCodeInstrument.getDirectSpoonInterfaces(CtFor.class).collect(Collectors.toList());
		assertThat(inter).containsExactly(CtLoop.class);
	}

	@Test
	void getAllSpoonInterfaces() {
		final List<Class<?>> inter = spoonCodeInstrument.getAllSpoonInterfaces(CtStatement.class).collect(Collectors.toList());
		assertThat(inter).containsExactlyInAnyOrder(CtCodeElement.class, CtElement.class, FactoryAccessor.class,
			CtVisitable.class, CtQueryable.class, SourcePositionHolder.class);
	}

	@Test
	void getUnqualifiedClassNameWithPkg() {
		final String name = spoonCodeInstrument.getUnqualifiedClassName("foo.bar.foobar.ClassName");
		assertThat(name).isEqualTo("ClassName");
	}

	@Test
	void getUnqualifiedClassNameWithoutPkg() {
		final String name = spoonCodeInstrument.getUnqualifiedClassName("CtClass");
		assertThat(name).isEqualTo("CtClass");
	}

	@Test
	void prettyPrintTypeClass() {
		final String string = spoonCodeInstrument.prettyPrintType(CtClass.class);
		assertThat(string).isEqualTo("CtClass");
	}

	@Test
	void prettyPrintTypeGeneric() throws NoSuchMethodException {
		final String string = spoonCodeInstrument.prettyPrintType(Stub.class.getMethod("foo").getGenericReturnType());
		assertThat(string).isEqualTo("Stub2<CtClass<?>>");
	}

	@Test
	void prettyPrintTypdeTwoNestedGenerics() throws NoSuchMethodException {
		final String string = spoonCodeInstrument.prettyPrintType(Stub.class.getMethod("bar").getGenericReturnType());
		assertThat(string).isEqualTo("Stub2<CtClass<String>>");
	}

	@Test
	void prettyPrintTypdeTwoGenerics() throws NoSuchMethodException {
		final String string = spoonCodeInstrument.prettyPrintType(Stub.class.getMethod("meth").getGenericReturnType());
		assertThat(string).isEqualTo("Stub3<CtStatement, ?>");
	}

	@Test
	void getSpoonProperties() {
		final StubSpoon stub = Mockito.mock(StubSpoon.class);

		Mockito.when(stub.foo()).thenReturn("fooo");
		Mockito.when(stub.getAnnotations()).thenReturn(List.of());
		Mockito.when(stub.getDocComment()).thenReturn("docc");
		Mockito.when(stub.getShortRepresentation()).thenReturn("text");
		Mockito.when(stub.getPosition()).thenReturn(null);
		Mockito.when(stub.isImplicit()).thenReturn(false);
		Mockito.when(stub.getReferencedTypes()).thenReturn(Set.of());
		Mockito.when(stub.getComments()).thenReturn(List.of());
		Mockito.when(stub.getParent()).thenReturn(null);

		final List<Pair<Class<?>, Set<Pair<String, String>>>> props = spoonCodeInstrument.getSpoonProperties(stub);

		assertThat(props).containsExactlyInAnyOrder(
			new Pair<>(StubSpoon.class, Set.of(new Pair<>("foo(): String", "fooo"))),
			new Pair<>(CtElement.class, Set.of(
					new Pair<>("getDocComment(): String", "docc"),
					new Pair<>("getAnnotations(): List<CtAnnotation<? extends java.lang.annotation.Annotation>>", "[]"),
					new Pair<>("getShortRepresentation(): String", "text"),
					new Pair<>("getPosition(): SourcePosition", "null"),
					new Pair<>("isImplicit(): boolean", "false"),
					new Pair<>("getReferencedTypes(): Set<CtTypeReference<?>>", "[]"),
					new Pair<>("getParent(): CtElement", "null"),
					new Pair<>("getComments(): List<CtComment>", "[]")))
		);
	}

	@Test
	void testCreateTable() {
		final Set<Pair<String, String>> data = Set.of(new Pair<>("foo(): String", "fooo"), new Pair<>("bar(): int", "1"));
		final TableView<Pair<String, String>> table = spoonCodeInstrument.createTable(data);

		assertThat(table).isNotNull();
		assertThat(table.getColumns().size()).isEqualTo(2);
		assertThat(table.getColumns().get(0).getText()).isEqualTo("Name");
		assertThat(table.getColumns().get(1).getText()).isEqualTo("Value");
		assertThat(table.getItems()).isEqualTo(new ArrayList<>(data));
	}

	@Test
	void testUpdatePropsPanel(final FxRobot robot) {
		final VBox propsPanel = robot.lookup("#propPanel").query();
		final CtElement stub = Mockito.mock(CtElement.class);

		Mockito.when(stub.getAnnotations()).thenReturn(List.of());
		Mockito.when(stub.getDocComment()).thenReturn("comment");
		Mockito.when(stub.getShortRepresentation()).thenReturn("rep");
		Mockito.when(stub.getPosition()).thenReturn(null);
		Mockito.when(stub.isImplicit()).thenReturn(true);
		Mockito.when(stub.getReferencedTypes()).thenReturn(Set.of());
		Mockito.when(stub.getComments()).thenReturn(List.of());
		Mockito.when(stub.getParent()).thenReturn(null);

		Platform.runLater(() -> spoonCodeInstrument.updatePropertiesPanel(stub));
		WaitForAsyncUtils.waitForFxEvents();

		assertThat(propsPanel.getChildren().size()).isEqualTo(1);
		assertThat(propsPanel.getChildren().get(0)).isInstanceOf(VBox.class);
		assertThat(((VBox) propsPanel.getChildren().get(0)).getChildren().size()).isEqualTo(2);
		assertThat(((VBox) propsPanel.getChildren().get(0)).getChildren().get(0)).isInstanceOf(Text.class);
		assertThat(((VBox) propsPanel.getChildren().get(0)).getChildren().get(1)).isInstanceOf(TableView.class);
	}

	@Test
	void testCleanPropertiesPanel(final FxRobot robot) {
		final VBox propsPanel = robot.lookup("#propPanel").query();
		final CtElement stub = Mockito.mock(CtElement.class);

		Platform.runLater(() -> spoonCodeInstrument.updatePropertiesPanel(stub));
		WaitForAsyncUtils.waitForFxEvents();
		Platform.runLater(() -> spoonCodeInstrument.cleanPropertiesPanel());
		WaitForAsyncUtils.waitForFxEvents();

		assertThat(propsPanel.getChildren()).isEmpty();
	}

	@Test
	void testCopy(final FxRobot robot) {
		final VBox propsPanel = robot.lookup("#propPanel").query();
		final CtElement stub = Mockito.mock(CtElement.class);
		final ClipboardContent clipboardContent = new ClipboardContent();
		clipboardContent.putString("");
		Platform.runLater(() -> Clipboard.getSystemClipboard().setContent(clipboardContent));
		WaitForAsyncUtils.waitForFxEvents();

		Platform.runLater(() -> spoonCodeInstrument.updatePropertiesPanel(stub));
		WaitForAsyncUtils.waitForFxEvents();

		robot.clickOn(((VBox) propsPanel.getChildren().get(0)).getChildren().get(1));
		robot.press(KeyCode.CONTROL).type(KeyCode.C).release(KeyCode.CONTROL);
		WaitForAsyncUtils.waitForFxEvents();

		final AtomicReference<Object> content = new AtomicReference<>();
		Platform.runLater(() -> content.set(Clipboard.getSystemClipboard().getContent(DataFormat.PLAIN_TEXT)));
		WaitForAsyncUtils.waitForFxEvents();

		AssertionsForClassTypes.assertThat(content.get()).isInstanceOf(String.class);
		AssertionsForClassTypes.assertThat((String) content.get()).isNotEmpty();
	}

	@Test
	void testCPath(final FxRobot robot) {
		final VBox propsPanel = robot.lookup("#propPanel").query();
		final CtElement stub = Mockito.mock(CtElement.class);
		final CtPath path = Mockito.mock(CtPath.class);
		Mockito.when(stub.getPath()).thenReturn(path);
		Mockito.when(path.toString()).thenReturn("#thePath");

		Platform.runLater(() -> spoonCodeInstrument.updatePropertiesPanel(stub));
		WaitForAsyncUtils.waitForFxEvents();

		final VBox box = (VBox) propsPanel.getChildren().get(0);
		assertThat(box.getChildren().get(0)).isInstanceOf(Text.class);
		assertThat(((Text) box.getChildren().get(0)).getText()).isEqualTo("Path");
		assertThat(box.getChildren().get(1)).isInstanceOf(TextField.class);
		assertThat(((TextField) box.getChildren().get(1)).getText()).isEqualTo("#thePath");
	}
}

interface StubSpoon extends CtElement {
	@PropertyGetter(role = ANNOTATION)
	String foo();
}

class Stub2<T extends CtElement>{

}

class Stub3<T extends CtElement, S>{

}

class Stub {
	public Stub2<CtClass<?>> foo() {
		return null;
	}

	public Stub2<CtClass<String>> bar() {
		return null;
	}

	public Stub3<CtStatement, ?> meth() {
		return null;
	}

}