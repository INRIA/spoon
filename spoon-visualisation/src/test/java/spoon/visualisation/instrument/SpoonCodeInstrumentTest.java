package spoon.visualisation.instrument;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import spoon.reflect.visitor.CtVisitable;
import spoon.reflect.visitor.chain.CtQueryable;

import static org.assertj.core.api.Assertions.assertThat;
import static spoon.reflect.path.CtRole.ANNOTATION;

@ExtendWith(ApplicationExtension.class)
class SpoonCodeInstrumentTest {
	SpoonCodeInstrument spoonCodeInstrument;

	@Start
	void start(final Stage stage) throws IOException {
		final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UI.fxml"));
		loader.load();
		spoonCodeInstrument = loader.getController();
		stage.setScene(new Scene(loader.getRoot()));
		stage.show();
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