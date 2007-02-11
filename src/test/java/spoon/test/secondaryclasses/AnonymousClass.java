package spoon.test.secondaryclasses;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;

public class AnonymousClass {

	private JButton foo;
	
	public void crashingMethod(){
		foo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showInputDialog("Boom!");
			}});
		}
}