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
package spoon.support.reflect.code;

import spoon.SpoonException;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLoop;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.visitor.CtInheritanceScanner;

import java.util.List;

public abstract class CtStatementImpl extends CtCodeElementImpl implements CtStatement {
	private static final long serialVersionUID = 1L;

	public static void insertAfter(CtStatement target, CtStatement statement)
	throws ParentNotInitializedException {
		CtStatementList sts = target.getFactory().Core().createStatementList();
		sts.addStatement(statement);
		insertAfter(target, sts);
	}

	public static void replace(CtStatement target, CtStatementList statements)
	throws ParentNotInitializedException {
		insertAfter(target, statements);
		CtElement e = target.getParent();
		CtStatementList parentStatementList = (CtStatementList) e;
		parentStatementList.removeStatement(target);
	}

	public static void insertAfter(CtStatement target, CtStatementList statements)
	throws ParentNotInitializedException {
		CtElement e = target.getParent();
		if (e instanceof CtExecutable) {
			throw new RuntimeException("cannot insert in this context (use insertEnd?)");
		}

		new InsertVisitor(target, statements, InsertType.AFTER).scan(e);
	}

	public static void insertBefore(CtStatement target, CtStatement statement)
	throws ParentNotInitializedException {
		CtStatementList sts = target.getFactory().Core().createStatementList();
		sts.addStatement(statement);
		insertBefore(target, sts);
	}

	public static void insertBefore(CtStatement target, CtStatementList statementsToBeInserted)
	throws ParentNotInitializedException {
		CtElement targetParent = target.getParent();
		if (targetParent instanceof CtExecutable) {
			throw new SpoonException("cannot insert in this context (use insertEnd?)");
		}
		if (target.getParent(CtConstructor.class) != null) {
			if (target instanceof CtInvocation
					&& ((CtInvocation<?>) target)
					.getExecutable()
					.getSimpleName()
					.startsWith("<init>")) {
				throw new SpoonException(
						"cannot insert a statement before a super or this invocation.");
			}
		}
		new InsertVisitor(target, statementsToBeInserted, InsertType.BEFORE).scan(targetParent);
	}

	private static class InsertVisitor extends CtInheritanceScanner {
		private final CtStatement target;
		private final CtStatementList statementsToBeInserted;
		private final InsertType insertType;

		InsertVisitor(CtStatement target, CtStatementList statementsToBeInserted, InsertType insertType) {
			this.target = target;
			this.statementsToBeInserted = statementsToBeInserted;
			this.insertType = insertType;
		}

		@Override
		public <R> void visitCtBlock(CtBlock<R> e) {
			super.visitCtBlock(e);

			insertType.insertFromFirstStatement(e, target, statementsToBeInserted);
		}

		@Override
		public void visitCtIf(CtIf e) {
			super.visitCtIf(e);

			boolean inThen = true;
			CtStatement stat = e.getThenStatement();
			if (stat != target) {
				stat = e.getElseStatement();
				inThen = false;
			}
			if (stat != target) {
				throw new IllegalArgumentException("should not happen");
			}
			if (stat instanceof CtBlock) {
				insertType.insert((CtBlock<?>) stat, statementsToBeInserted);
			} else {
				CtBlock<?> block = insertNewBlock(stat);
				if (inThen) {
					e.setThenStatement(block);
				} else {
					e.setElseStatement(block);
				}
			}
		}

		@Override
		public <E> void visitCtSwitch(CtSwitch<E> e) {
			super.visitCtSwitch(e);

			for (CtStatement s : statementsToBeInserted) {
				if (!(s instanceof CtCase)) {
					throw new RuntimeException("cannot insert something that is not case in a switch");
				}
			}

			insertType.insertFromLastStatement(e.getCases(), target, statementsToBeInserted);
		}

		@Override
		public <E> void visitCtCase(CtCase<E> e) {
			super.visitCtCase(e);

			target.setParent(e);
			insertType.insertFromLastStatement(e.getStatements(), target, statementsToBeInserted);
		}

		@Override
		public void scanCtLoop(CtLoop loop) {
			super.scanCtLoop(loop);

			CtStatement stat = loop.getBody();
			if (stat instanceof CtBlock) {
				insertType.insert((CtBlock<?>) stat, statementsToBeInserted);
			} else {
				CtBlock<?> block = insertNewBlock(stat);
				target.setParent(block);
				loop.setBody(block);
			}
		}

		private CtBlock<?> insertNewBlock(CtStatement stat) {
			CtBlock<?> block = target.getFactory().Core().createBlock();
			block.addStatement(stat);
			insertType.insertFromFirstStatement(block, target, statementsToBeInserted);
			return block;
		}
	}

	private enum InsertType {
		BEFORE {
			@Override
			void insert(CtBlock<?> block, CtStatementList statementsToBeInserted) {
				block.insertBegin(statementsToBeInserted);
			}

			@Override
			void insertFromFirstStatement(CtBlock<?> block, CtStatement target,
					CtStatementList statementsToBeInserted) {
				// check the reference not the equality
				int indexOfTargetElement = indexOfReference(block.getStatements(), target);
				for (CtStatement s : statementsToBeInserted) {
					s.setParent(block);
					block.getStatements().add(indexOfTargetElement++, s);
				}
			}

			@Override
			<T extends CtElement> void insertFromLastStatement(List<T> statements, CtStatement target, CtStatementList statementsToBeInserted) {
				// check the reference not the equality
				int indexOfTargetElement = indexOfReference(statements, target);
				for (int j = statementsToBeInserted.getStatements().size() - 1; j >= 0; j--) {
					final CtStatement newStatement = statementsToBeInserted.getStatements().get(j);
					newStatement.setParent(statements.get(indexOfTargetElement).getParent());
					statements.add(indexOfTargetElement, (T) newStatement);
				}
			}
		},
		AFTER {
			@Override
			void insert(CtBlock<?> block, CtStatementList statementsToBeInserted) {
				block.insertEnd(statementsToBeInserted);
			}

			@Override
			void insertFromFirstStatement(CtBlock<?> block, CtStatement target, CtStatementList statementsToBeInserted) {
				// check the reference not the equality
				int indexOfTargetElement = indexOfReference(block.getStatements(), target);
				for (CtStatement s : statementsToBeInserted) {
					s.setParent(block);
					block.getStatements().add(++indexOfTargetElement, s);
				}
			}

			@Override
			<T extends CtElement> void insertFromLastStatement(List<T> statements, CtStatement target, CtStatementList statementsToBeInserted) {
				int indexOfTargetElement = indexOfReference(statements, target) + 1;
				for (int j = statementsToBeInserted.getStatements().size() - 1; j >= 0; j--) {
					final CtStatement newStatement = statementsToBeInserted.getStatements().get(j);
					newStatement.setParent(target.getParent());
					statements.add(indexOfTargetElement, (T) newStatement);
				}
			}
		};

		public int indexOfReference(List statements, CtElement target) {
			int indexOfTargetElement = -1;
			// check the reference not the equality
			for (int i = 0; i < statements.size(); i++) {
				if (statements.get(i) == target) {
					indexOfTargetElement = i;
					break;
				}
			}
			return indexOfTargetElement;
		}
		abstract void insert(CtBlock<?> block, CtStatementList statementsToBeInserted);
		abstract void insertFromFirstStatement(CtBlock<?> block, CtStatement target, CtStatementList statementsToBeInserted);
		abstract <T extends CtElement> void insertFromLastStatement(List<T> statements, CtStatement target, CtStatementList statementsToBeInserted);
	}

	@Override
	public <T extends CtStatement> T insertBefore(CtStatement statement) throws ParentNotInitializedException {
		insertBefore(this, statement);
		return (T) this;
	}

	@Override
	public <T extends CtStatement> T insertBefore(CtStatementList statements) throws ParentNotInitializedException {
		insertBefore(this, statements);
		return (T) this;
	}

	@Override
	public <T extends CtStatement> T insertAfter(CtStatement statement) throws ParentNotInitializedException {
		insertAfter(this, statement);
		return (T) this;
	}

	@Override
	public <T extends CtStatement> T insertAfter(CtStatementList statements) throws ParentNotInitializedException {
		insertAfter(this, statements);
		return (T) this;
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

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public <T extends CtStatement> T setLabel(String label) {
		this.label = label;
		return (T) this;
	}

	@Override
	public void replace(CtStatement element) {
		replace((CtElement) element);
	}
}
