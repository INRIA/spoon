package spoon.test.lambda.testclasses;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ToggleButton;

/**
 * Created by nicolas on 16/07/2015.
 */
public class JavaFx {

	public void test() {
		ListChangeListener.Change<? extends ToggleButton> change = null;
		FXCollections.sort(change.getList(),
				(o1, o2) -> {
					return 0;
				});
	}

}
