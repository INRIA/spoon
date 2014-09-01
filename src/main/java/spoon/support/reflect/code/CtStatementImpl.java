/* 
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
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

package spoon.support.reflect.code;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLoop;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.ParentNotInitializedException;

public abstract class CtStatementImpl extends CtCodeElementImpl implements
		CtStatement {
	private static final long serialVersionUID = 1L;

	public static void insertAfter(CtStatement target, CtStatement statement)
			throws ParentNotInitializedException {
		CtStatementList sts = target.getFactory().Core()
				.createStatementList();
		sts.addStatement(statement);
		insertAfter(target, sts);
	}

	public static void replace(CtStatement target, CtStatementList statements)
			throws ParentNotInitializedException {
		insertAfter(target, statements);
		CtBlock<?> parentBlock = (CtBlock<?>) target.getParent();
		parentBlock.removeStatement(target);
	}

	public static void insertAfter(CtStatement target,
			CtStatementList statements) throws ParentNotInitializedException {
		CtElement e = target.getParent();
		if (e instanceof CtExecutable) {
			throw new RuntimeException(
					"cannot insert in this context (use insertEnd?)");
		}
		CtStatementList parentStatementList = (CtStatementList) e;
		int i = 0;
		for (CtStatement s : parentStatementList.getStatements()) {
			i++;
			if (s == target) {
				break;
			}
		}
		for (int j = statements.getStatements().size() - 1; j >= 0; j--) {
			CtStatement s = statements.getStatements().get(j);
			parentStatementList.getStatements().add(i, s);
		}
	}

	public static void insertBefore(CtStatement target, CtStatement statement)
			throws ParentNotInitializedException {
		CtStatementList sts = target.getFactory().Core()
				.createStatementList();
		sts.addStatement(statement);
		insertBefore(target, sts);
	}

	public static void insertBefore(CtStatement target,
			CtStatementList statementsToBeInserted) throws ParentNotInitializedException {
		CtElement targetParent = target.getParent();
		if (targetParent instanceof CtExecutable) {
			throw new RuntimeException(
					"cannot insert in this context (use insertEnd?)");
		}
		CtBlock<?> parentBlock;
		if (targetParent instanceof CtIf) {
			boolean inThen = true;
			CtStatement stat = ((CtIf) targetParent).getThenStatement();
			if (stat != target) {
				stat = ((CtIf) targetParent).getElseStatement();
				inThen = false;
			}
			if (stat != target) {
				throw new IllegalArgumentException("should not happen");
			}
			if (stat instanceof CtBlock) {
                ((CtBlock<?>) stat).insertBegin(statementsToBeInserted);
                return;
			} else {
				CtBlock<?> block = target.getFactory().Core().createBlock();
				block.addStatement(stat);
				if (inThen)
					((CtIf) targetParent).setThenStatement(block);
				else
					((CtIf) targetParent).setElseStatement(block);
				parentBlock = block;
			}
		} else if (targetParent instanceof CtSwitch) {
            for (CtStatement s : statementsToBeInserted) {
                if (! (s instanceof CtCase)) {
                    throw new RuntimeException(
                            "cannot insert something that is not case in a switch");
                }
            }
            int i=0;
            for (CtStatement s : ((CtSwitch<?>) targetParent).getCases()) {
                if (s == target) {
                    break;
                }
                i++;
            }
            for (int j = statementsToBeInserted.getStatements().size() - 1; j >= 0; j--) {
                CtStatement s = statementsToBeInserted.getStatements().get(j);
                ((CtSwitch<?>) targetParent).getCases().add(i, (CtCase)s);
            }
            return;
        } else if (targetParent instanceof CtLoop) {
			CtStatement stat = ((CtLoop) targetParent).getBody();
			if (stat instanceof CtBlock) {
				parentBlock = (CtBlock<?>) stat;
                ((CtBlock<?>) stat).insertBegin(statementsToBeInserted);
                return;
			} else {
				CtBlock<?> block = target.getFactory().Core().createBlock();
				block.getStatements().add(stat);
				((CtLoop) targetParent).setBody(block);
				parentBlock = block;
			}
		} else if (targetParent instanceof CtCase) {
            int i=0;
            for (CtStatement s : ((CtCase<?>) targetParent).getStatements()) {
                if (s == target) {
                    break;
                }
                i++;
            }
            for (int j = statementsToBeInserted.getStatements().size() - 1; j >= 0; j--) {
                CtStatement s = statementsToBeInserted.getStatements().get(j);
                ((CtCase<?>) targetParent).getStatements().add(i, s);
            }
            return;
        } else {
			parentBlock = (CtBlock<?>) targetParent;// BCUTAG bad cast
		}

        if (!(parentBlock instanceof CtBlock)) {
            throw new RuntimeException("cannot add a statement that is not in a block");
        }

        int indexOfTargetElement = 0;
		for (CtStatement s : parentBlock.getStatements()) {
			if (s == target) {
				break;
			}
			indexOfTargetElement++;
		}
		for (CtStatement s : statementsToBeInserted) {
			parentBlock.getStatements().add(indexOfTargetElement++, s);
		}
	}

	public void insertBefore(CtStatement statement)
			throws ParentNotInitializedException {
		insertBefore(this, statement);
	}

	public void insertBefore(CtStatementList statements)
			throws ParentNotInitializedException {
		insertBefore(this, statements);
	}

	public void insertAfter(CtStatement statement)
			throws ParentNotInitializedException {
		insertAfter(this, statement);
	}

	public void insertAfter(CtStatementList statements)
			throws ParentNotInitializedException {
		insertAfter(this, statements);
	}

	@Override
	public void replace(CtElement element) {
		if (element instanceof CtStatementList) {
			CtStatementImpl.replace(this, (CtStatementList) element);
		} else {
			super.replace(element);
		}
	}

	String label;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
