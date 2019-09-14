package spoon.visualisation;

import io.github.interacto.command.CmdHandler;
import io.github.interacto.command.CommandsRegistry;
import io.github.interacto.command.library.OpenWebPage;
import io.github.interacto.fsm.TimeoutTransition;
import java.io.IOException;
import java.util.Set;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;
import spoon.visualisation.command.TreeLevel;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(ApplicationExtension.class)
public class ShowMeTest {
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
		final Parent root = FXMLLoader.load(getClass().getResource("/fxml/UI.fxml"));
		stage.setScene(new Scene(root));
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

		assertThat(ast.size()).isEqualTo(1);
		assertThat(ast.get(0).getValue()
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

		assertThat(ast.size()).isEqualTo(1);
		assertThat(ast.get(0).getValue()
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

		assertThat(ast.size()).isEqualTo(1);
		assertThat(ast.get(0).getValue()
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

		assertThat(ast.size()).isEqualTo(1);
		assertThat(ast.get(0).getValue()
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

		assertThat(item.getValue()
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

		assertThat(item).isNull();
	}

	@Test
	void testNumberOfHyperlinks(final FxRobot robot) {
		robot.clickOn(spoonCode).write("class Foo { } class Bar {}");
		waitForTimeoutTransitions();
		final Set<Node> hyperlinks = robot.lookup(CoreMatchers.instanceOf(Hyperlink.class)).queryAll();

		assertThat(hyperlinks.size()).isEqualTo(2);
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
}
