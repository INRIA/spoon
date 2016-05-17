package spoon.test.lambda.testclasses;

import javax.swing.JButton;

public class Kuu {
	public void foo() {
		JButton b = new JButton();
		b.addActionListener(e -> {
			System.out.println(e);
		});
	}
}
