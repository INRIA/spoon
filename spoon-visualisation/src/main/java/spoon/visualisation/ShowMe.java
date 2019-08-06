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
package spoon.visualisation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import spoon.visualisation.command.TreeLevel;
import spoon.visualisation.command.UpdateSpoonTree;

/**
 * The main JavaFX class of the app.
 */
public class ShowMe extends Application {
	public static void main(final String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage primaryStage) throws IOException {
		// Loading the JFX view
		final Parent root = FXMLLoader.load(getClass().getResource("/fxml/UI.fxml"));
		primaryStage.setScene(new Scene(root));
		primaryStage.show();

		// Can have an argument: a java file to load
		final Parameters params = getParameters();
		if(!params.getUnnamed().isEmpty()) {
			final List<String> code = Files.readAllLines(Paths.get(new File(params.getUnnamed().get(0)).toURI()));
			final Node area = root.lookup("#spoonCode");
			final Node tree = root.lookup("#spoonAST");
			if(area instanceof TextArea && tree instanceof TreeView) {
				final TextArea spoonCode = (TextArea) area;
				spoonCode.setText(String.join(System.getProperty("line.separator"), code));
				new UpdateSpoonTree((TreeView<String>) tree, true, spoonCode.getText(), TreeLevel.AUTO).doIt();
			}
		}
	}
}