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
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLoop;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;

public abstract class CtStatementImpl extends CtCodeElementImpl implements
		CtStatement {
	private static final long serialVersionUID = 1L;

	public static void insertAfter(CtStatement target, CtStatement statement) {
		CtStatementList<?> sts = target.getFactory().Core()
				.createStatementList();
		sts.getStatements().add(statement);
		insertAfter(target, sts);
	}

	public static void replace(CtStatement target, CtStatementList<?> statements) {
		insertAfter(target, statements);
		CtBlock<?> parentBlock = (CtBlock<?>) target.getParent();
		parentBlock.getStatements().remove(target);
	}

	public static void insertAfter(CtStatement target,
			CtStatementList<?> statements) {
		CtElement e = target.getParent();
		if (e instanceof CtExecutable) {
			throw new RuntimeException(
					"cannot insert in this context (use insertEnd?)");
		}
		CtBlock<?> parentBlock = (CtBlock<?>) e;
		int i = 0;
		for (CtStatement s : parentBlock.getStatements()) {
			i++;
			if (s == target) {
				break;
			}
		}
		for (int j = statements.getStatements().size() - 1; j >= 0; j--) {
			CtStatement s = statements.getStatements().get(j);
			parentBlock.getStatements().add(i, s);
			s.setParent(parentBlock);
		}
	}

	public static void insertBefore(CtStatement target, CtStatement statement) {
		CtStatementList<?> sts = target.getFactory().Core()
				.createStatementList();
		sts.getStatements().add(statement);
		insertBefore(target, sts);
	}

	public static void insertBefore(CtStatement target,
			CtStatementList<?> statements) {
		CtElement e = target.getParent();
		if (e instanceof CtExecutable) {
			throw new RuntimeException(
					"cannot insert in this context (use insertEnd?)");
		}
		int i = 0;
		CtBlock<?> parentBlock;
		if(e instanceof CtIf){
			boolean inThen=true;
			CtStatement stat = ((CtIf) e).getThenStatement();
			if(stat!=target){
				stat = ((CtIf) e).getElseStatement();
				inThen=false;
			}
			if(stat!=target){
				throw new IllegalArgumentException("should not happen");
			}
			if(stat instanceof CtBlock){
				parentBlock = (CtBlock<?>) stat;
			}else{
				CtBlock<?> block = target.getFactory().Core().createBlock();
				block.getStatements().add(stat);
				stat.setParent(block);
				if(inThen)
					((CtIf) e).setThenStatement(block);
				else
					((CtIf) e).setElseStatement(block);
				block.setParent(e);
				parentBlock = block;
			}
		}else if(e instanceof CtLoop){
			CtStatement stat = ((CtLoop) e).getBody();
			if(stat instanceof CtBlock){
				parentBlock = (CtBlock<?>) stat;
			}else{
				CtBlock<?> block = target.getFactory().Core().createBlock();
				block.getStatements().add(stat);
				stat.setParent(block);
				((CtLoop) e).setBody(block);
				block.setParent(e);
				parentBlock = block;
			}
		}else if(e instanceof CtCase){
			for (CtStatement s : ((CtCase<?>) e).getStatements()) {
				if (s == target) {
					break;
				}
				i++;
			}
			for (int j = statements.getStatements().size() - 1; j >= 0; j--) {
				CtStatement s = statements.getStatements().get(j);
				((CtCase<?>) e).getStatements().add(i, s);
			}
			return;
		}else{
			parentBlock = (CtBlock<?>) e;//BCUTAG bad cast
		}
		for (CtStatement s : parentBlock.getStatements()) {
			if (s == target) {
				break;
			}
			i++;
		}
		for (int j = statements.getStatements().size() - 1; j >= 0; j--) {
			CtStatement s = statements.getStatements().get(j);
			parentBlock.getStatements().add(i, s);
			s.setParent(parentBlock);
		}
	}

	public void insertBefore(CtStatement statement) {
		insertBefore(this, statement);
	}

	public void insertBefore(CtStatementList<?> statements) {
		insertBefore(this, statements);
	}

	public void insertAfter(CtStatement statement) {
		insertAfter(this, statement);
	}

	public void insertAfter(CtStatementList<?> statements) {
		insertAfter(this, statements);
	}

	public void replace(CtElement element) {
		if (element instanceof CtStatementList) {
			CtStatementImpl.replace(this, (CtStatementList<?>) element);
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
