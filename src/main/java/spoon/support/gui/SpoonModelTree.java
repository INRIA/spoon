/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import spoon.Launcher;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.support.SerializationModelStreamer;

public class SpoonModelTree extends JFrame implements KeyListener,
		MouseListener {

	private static final long serialVersionUID = 1L;

	Enumeration<?> enume;

	private Factory factory;

	private JPanel jContentPane = null;

	private JScrollPane jScrollPane = null;

	private JTree jTree = null;

	JPopupMenu menu;

	private DefaultMutableTreeNode root; // @jve:decl-index=0:visual-constraint="207,57"

	String searchValue;

	/**
	 * This is the default constructor
	 */
	public SpoonModelTree(Factory factory) {
		SpoonTreeBuilder cst = new SpoonTreeBuilder();
		cst.scan(factory.Package().getRootPackage());
		this.factory = factory;
		root = cst.getRoot();
		initialize();
	}

	public SpoonModelTree(CtElement rootElement) {
		SpoonTreeBuilder cst = new SpoonTreeBuilder();
		cst.scan(rootElement);
		this.factory = rootElement.getFactory();
		root = cst.getRoot();
		initialize();
	}

	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(1);
			jContentPane = new JPanel();
			jContentPane.setLayout(gridLayout);
			jContentPane.add(getJScrollPane(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTree());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTree
	 *
	 * @return javax.swing.JTree
	 */
	private JTree getJTree() {
		if (jTree == null) {
			jTree = new JTree(root);
			jTree.addKeyListener(this);
			jTree.addMouseListener(this);
		}
		return jTree;
	}

	private JPopupMenu getMenu() {
		if (menu == null) {
			menu = new JPopupMenu();

			JMenuItem item = new JMenuItem("Save");
			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					JFileChooser chooser = new JFileChooser();
					chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					boolean cont = chooser.showSaveDialog(SpoonModelTree.this) == JFileChooser.APPROVE_OPTION;
					if (cont) {
						SerializationModelStreamer ser = new SerializationModelStreamer();
						try {
							ser.save(factory, new FileOutputStream(chooser
									.getSelectedFile()));
						} catch (IOException e1) {
							Launcher.LOGGER.error(e1.getMessage(), e1);
						}
					}
				}
			});

			menu.add(item);
			menu.addSeparator();

			// show reflect table
			item = new JMenuItem("Reflect");
			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree
							.getLastSelectedPathComponent();
					if (node == null) {
						node = root;
					}
					new SpoonObjectFieldsTable(node.getUserObject());
				}
			});
			menu.add(item);

			menu.addSeparator();

			// Search value
			item = new JMenuItem("Search");
			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					search();
				}
			});
			menu.add(item);

			// Search value
			item = new JMenuItem("Search next");
			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					next();
				}
			});
			menu.add(item);

			menu.addSeparator();

			// Expand all
			item = new JMenuItem("Expand all");
			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree
							.getLastSelectedPathComponent();
					if (node == null) {
						node = root;
					}
					expandAll(node);
				}
			});
			menu.add(item);
		}
		return menu;
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		this.setSize(640, 480);
		this.setLocation((getGraphicsConfiguration().getDevice()
				.getDisplayMode().getWidth() - getWidth()) / 2,
				(getGraphicsConfiguration().getDevice().getDisplayMode()
						.getHeight() - getHeight()) / 2);

		setContentPane(getJContentPane());
		setTitle("Spoon");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {
		switch (e.getKeyChar()) {
		case ('s'):

			break;
		case ('n'):
			next();
			break;
		case ('o'):
			if (jTree.getLastSelectedPathComponent() != null) {
				new SpoonObjectFieldsTable(((DefaultMutableTreeNode) jTree
						.getLastSelectedPathComponent()).getUserObject());
			}
			break;
		}
	}

	private void maybeShowPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			getMenu().show(e.getComponent(), e.getX(), e.getY());
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		getJTree().setSelectionRow(
				getJTree().getClosestRowForLocation(e.getX(), e.getY()));
		maybeShowPopup(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		maybeShowPopup(e);
	}

	/** move to the next node matching the search criterion */
	public DefaultMutableTreeNode next() {
		DefaultMutableTreeNode current;
		while ((enume != null) && enume.hasMoreElements()) {
			current = (DefaultMutableTreeNode) enume.nextElement();
			if ((current.getUserObject() != null)
					&& current.getUserObject().toString().contains(searchValue)) {
				setVisible(current);
				return current;
			}
		}
		return null;
	}

	/** shows a dialog to enter the value to search for in the AST */
	public DefaultMutableTreeNode search() {
		searchValue = JOptionPane.showInputDialog(this,
				"Enter value to search:", "Search");

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) jTree
				.getLastSelectedPathComponent();
		if (node == null) {
			node = root;
		}
		enume = node.depthFirstEnumeration();

		if (searchValue != null) {
			return next();
		}
		return null;
	}

	/** expand all AST nodes in the GUI */
	public DefaultMutableTreeNode expandAll(final DefaultMutableTreeNode node) {
		if (node == null || node.isLeaf()) {
			return null;
		}
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Queue<DefaultMutableTreeNode> q = new LinkedList<>();
					q.add(node);
					while (!q.isEmpty()) {
						final DefaultMutableTreeNode n = q.poll();
						expand(n);
						@SuppressWarnings("unchecked")
						Enumeration<TreeNode> children = n.children();
						while (children.hasMoreElements()) {
							DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.nextElement();
							if (!child.isLeaf() && child.getChildCount() > 0) {
								q.offer(child);
							}
						}
					}
				} finally {
					executor.shutdownNow();
				}
			}
		});
		return node;
	}

	public void expand(final DefaultMutableTreeNode node) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				TreePath path = new TreePath(node.getPath());
				if (!jTree.isExpanded(path)) {
					jTree.expandPath(path);
					jTree.updateUI();
				}
			}
		});
	}

	public void setVisible(DefaultMutableTreeNode node) {
		TreePath path = new TreePath(node.getPath());
		getJTree().scrollPathToVisible(path);
		getJTree().setSelectionPath(path);
	}

}
