package spoon.test.secondaryclasses.testclasses;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;

public class AnonymousClass {

	public interface I {

	}

	private JButton foo;

	public void crashingMethod() {
		foo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showInputDialog("Boom!");
			}
		});
	}

	public void annonymousCreation() {
		new I() {
		};
	}

}