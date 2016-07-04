import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

class Foo3 {
	final JButton b = new JButton();
	final JButton a = new JButton();
	public Foo() {
		b.addActionListener(e -> {
			if(e.getSource()==a) {
				System.out.println(((JButton) e.getSource()).getName()); // Command 1
			}
		});
	}
}