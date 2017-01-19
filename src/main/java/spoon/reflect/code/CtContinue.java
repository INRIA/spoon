/**
 * Copyright (C) 2006-2017 INRIA and contributors
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
package spoon.reflect.code;

/**
 * This code element defines the continue statement.
 * Example:
 * <pre>
 *     for(int i=0; i&lt;10; i++) {
 *         if (i&gt;3) {
 *				continue; // &lt;-- continue statement
 *         }
 *     }
 * </pre>
 */
public interface CtContinue extends CtCFlowBreak {
	/**
	 * Gets the statement where the control flow continues (null if no label
	 * defined).
	 */
	CtStatement getLabelledStatement();

	/**
	 * Sets the statement where the control flow continues (null if no label
	 * defined).
	 */
	<T extends CtContinue> T setLabelledStatement(CtStatement labelledStatement);

	/**
	 * Gets the label from which the control flow breaks (null if no label
	 * defined).
	 */
	String getTargetLabel();

	/**
	 * Sets the label from which the control flow breaks (null if no label
	 * defined).
	 */
	<T extends CtContinue> T setTargetLabel(String targetLabel);

	@Override
	CtContinue clone();
}
