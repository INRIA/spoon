package spoon.visualisation;

import io.github.interacto.fsm.TimeoutTransition;
import java.io.IOException;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;
import spoon.visualisation.command.TreeLevel;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(ApplicationExtension.class)
public class ShowMeTest {
	TreeView<String> spoonAST;
	TextArea spoonCode;
	ComboBox<TreeLevel> treeLevel;
	CheckBox hideImplicit;

	static void waitForTimeoutTransitions() {
		Thread.getAllStackTraces().keySet()
			.stream()
			.filter(thread -> thread.getName().startsWith(TimeoutTransition.TIMEOUT_THREAD_NAME_BASE))
			.forEach(thread -> {
				try {
					thread.join();
				}catch(final InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
			});
		WaitForAsyncUtils.waitForFxEvents();
	}

	@Start
	void start(final Stage stage) throws IOException {
		final Parent root = FXMLLoader.load(getClass().getResource("/fxml/UI.fxml"));
		stage.setScene(new Scene(root));
		stage.show();
	}

	@BeforeEach
	void setUp(final FxRobot robot) {
		spoonAST = robot.lookup("#spoonAST").query();
		spoonCode = robot.lookup("#spoonCode").query();
		treeLevel = robot.lookup("#treeLevel").query();
		hideImplicit = robot.lookup("#hideImplicit").query();
	}

	@Test
	void testTypeCodeCreatesTheAST(final FxRobot robot) {
		robot.clickOn(spoonCode).write("class Foo { }");
		waitForTimeoutTransitions();
		final ObservableList<TreeItem<String>> ast = spoonAST.getRoot().getChildren();
		assertThat(ast.size()).isEqualTo(1);
		assertThat(ast.get(0).getValue()).contains("Foo");
	}

	@Test
	void testClassElementLevel(final FxRobot robot) {
		robot.clickOn(spoonCode).write("int i;");
		waitForTimeoutTransitions();
		robot.clickOn(treeLevel).type(KeyCode.DOWN).type(KeyCode.ENTER);
		WaitForAsyncUtils.waitForFxEvents();
		final ObservableList<TreeItem<String>> ast = spoonAST.getRoot().getChildren();
		assertThat(ast.size()).isEqualTo(1);
		assertThat(ast.get(0).getValue()).contains("Field");
	}

	@Test
	void testStatementLevel(final FxRobot robot) {
		robot.clickOn(spoonCode).write("int i;");
		waitForTimeoutTransitions();
		robot.clickOn(treeLevel).type(KeyCode.DOWN).type(KeyCode.DOWN).type(KeyCode.ENTER);
		WaitForAsyncUtils.waitForFxEvents();
		final ObservableList<TreeItem<String>> ast = spoonAST.getRoot().getChildren();
		assertThat(ast.size()).isEqualTo(1);
		assertThat(ast.get(0).getValue()).contains("LocalVariable");
	}

	@Test
	void testExpLevel(final FxRobot robot) {
		robot.clickOn(spoonCode).write("1 < 2");
		waitForTimeoutTransitions();
		robot.clickOn(treeLevel).type(KeyCode.DOWN).type(KeyCode.DOWN).type(KeyCode.DOWN).type(KeyCode.ENTER);
		WaitForAsyncUtils.waitForFxEvents();
		final ObservableList<TreeItem<String>> ast = spoonAST.getRoot().getChildren();
		assertThat(ast.size()).isEqualTo(1);
		assertThat(ast.get(0).getValue()).contains("Binary");
	}

	@Test
	void testShowImplicit(final FxRobot robot) {
		robot.clickOn(hideImplicit);
		robot.clickOn(spoonCode).write("class Foo { }");
		waitForTimeoutTransitions();
		final TreeItem<String> item = spoonAST.getTreeItem(1);
		assertThat(item.getValue()).contains("(implicit)");
	}

	@Test
	void testHideImplicit(final FxRobot robot) {
		robot.clickOn(spoonCode).write("class Foo { }");
		waitForTimeoutTransitions();
		final TreeItem<String> item = spoonAST.getTreeItem(1);
		assertThat(item).isNull();
	}
}
