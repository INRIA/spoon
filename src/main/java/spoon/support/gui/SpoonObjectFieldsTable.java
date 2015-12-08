/**
 * Copyright (C) 2006-2015 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
package spoon.support.gui;

import java.awt.BorderLayout;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import spoon.Launcher;

public class SpoonObjectFieldsTable extends JFrame {
	public class SpoonObjectTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		List<Field> field;

		Object o;

		public SpoonObjectTableModel(Object o) {
			super();

			this.o = o;
			field = new ArrayList<Field>();

			scanFields(o.getClass());
		}

		public int getColumnCount() {
			return columnsName.length;
		}

		@Override
		public String getColumnName(int column) {
			return columnsName[column];
		}

		public int getRowCount() {
			return field.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			Field m = field.get(rowIndex);
			switch (columnIndex) {
			case (0):
				return m.getName();
			case (1):
				return m.getType().getCanonicalName();
			case (2):
				try {
					Object val = m.get(o);
					if (val != null) {
						return val.getClass().getCanonicalName();
					}
				} catch (IllegalArgumentException e) {
					Launcher.LOGGER.error(e.getMessage(), e);
				} catch (IllegalAccessException e) {
					Launcher.LOGGER.error(e.getMessage(), e);
				}
				break;
			case (3):
				try {
					return m.get(o);
				} catch (IllegalArgumentException e) {
					Launcher.LOGGER.error(e.getMessage(), e);
				} catch (IllegalAccessException e) {
					Launcher.LOGGER.error(e.getMessage(), e);
				}
			}
			return null;
		}

		public void scanFields(Class<?> c) {
			for (Field f : c.getDeclaredFields()) {
				f.setAccessible(true);
				if (!Modifier.isStatic(f.getModifiers())) {
					field.add(f);
				}
			}
			if (c.getSuperclass() != null) {
				scanFields(c.getSuperclass());
			}
		}
	}

	public static final String[] columnsName = new String[] {
			"Name", "FieldType", "currentType", "Value"
	};

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JScrollPane jScrollPane = null;

	private JTable jTable = null;

	private Object o;

	/**
	 * This is the default constructor
	 */
	public SpoonObjectFieldsTable(Object o) {
		super();
		this.o = o;
		initialize();
	}

	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
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
			jScrollPane.setViewportView(getJTable());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTable
	 *
	 * @return javax.swing.JTable
	 */
	private JTable getJTable() {
		if (jTable == null) {
			jTable = new JTable(new SpoonObjectTableModel(o));
		}
		return jTable;
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		this.setSize(320, 240);
		this.setLocation((getGraphicsConfiguration().getDevice().getDisplayMode().getWidth() - getWidth()) / 2,
				(getGraphicsConfiguration().getDevice().getDisplayMode().getHeight() - getHeight()) / 2);
		this.setContentPane(getJContentPane());
		this.setTitle(o.getClass().getSimpleName());
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

}
