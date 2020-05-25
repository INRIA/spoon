/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.code;

import spoon.SpoonException;
import spoon.reflect.annotations.MetamodelPropertyField;
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
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.CtInheritanceScanner;

import java.util.ArrayList;
import java.util.List;

import static spoon.reflect.path.CtRole.LABEL;

public abstract class CtStatementImpl extends CtCodeElementImpl implements CtStatement {
	private static final long serialVersionUID = 1L;

	public static void insertAfter(CtStatement target, CtStatement statement)
	throws ParentNotInitializedException {
		CtStatementList sts = target.getFactory().Core().createStatementList();
		sts.addStatement(statement);
		insertAfter(target, sts);
	}

	public static void insertAfter(CtStatement target, CtStatementList statements)
	throws ParentNotInitializedException {
		CtElement e = target.getParent();
		if (e instanceof CtExecutable) {
			throw new RuntimeException("cannot insert in this context (use insertEnd?)");
		}

		new InsertVisitor(target, statements, InsertType.AFTER).scan(e);
	}

	/** insert `statement` just before target */
	public static void insertBefore(CtStatement target, CtStatement statement)
	throws ParentNotInitializedException {
		CtStatementList sts = target.getFactory().Core().createStatementList();
		sts.addStatement(statement);
		insertBefore(target, sts);
	}

	/** inserts all statements of `statementsToBeInserted` just before `target` */
	public static void insertBefore(CtStatement target, CtStatementList statementsToBeInserted)
	throws ParentNotInitializedException {
		CtElement targetParent = target.getParent();
		if (targetParent instanceof CtExecutable) {
			throw new SpoonException("cannot insert in this context (use insertEnd?)");
		}
		try {
			if (target.getParent(CtConstructor.class) != null) {
				if (target instanceof CtInvocation && ((CtInvocation<?>) target).getExecutable().getSimpleName().startsWith(CtExecutableReference.CONSTRUCTOR_NAME)) {
					throw new SpoonException("cannot insert a statement before a super or this invocation.");
				}
			}
		} catch (ParentNotInitializedException ignore) {
			// no parent set somewhere
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

			e.setCases(insertType.insertFromLastStatement(e.getCases(), target, statementsToBeInserted));
		}

		@Override
		public <E> void visitCtCase(CtCase<E> e) {
			super.visitCtCase(e);

			target.setParent(e);
			e.setStatements(insertType.insertFromLastStatement(e.getStatements(), target, statementsToBeInserted));
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
			void insertFromFirstStatement(CtBlock<?> block, CtStatement target, CtStatementList statementsToBeInserted) {
				final List<CtStatement> copy = new ArrayList<>(block.getStatements());
				int indexOfTargetElement = indexOfReference(block.getStatements(), target);
				for (CtStatement ctStatement : statementsToBeInserted) {
					copy.add(indexOfTargetElement++, ctStatement);
				}
				//remove statements from the `statementsToBeInserted` before they are added to spoon model
				//note: one element MUST NOT be part of two models.
				statementsToBeInserted.setStatements(null);
				block.setStatements(copy);
			}

			@Override
			<T extends CtElement> List<T> insertFromLastStatement(List<T> statements, CtStatement target, CtStatementList statementsToBeInserted) {
				final List<T> copy = new ArrayList<>(statements);
				int indexOfTargetElement = indexOfReference(statements, target);
				for (int j = statementsToBeInserted.getStatements().size() - 1; j >= 0; j--) {
					copy.add(indexOfTargetElement, (T) statementsToBeInserted.getStatements().get(j));
				}
				//remove statements from the `statementsToBeInserted` before they are added to spoon model
				//note: one element MUST NOT be part of two models.
				statementsToBeInserted.setStatements(null);
				return copy;
			}
		},
		AFTER {
			@Override
			void insert(CtBlock<?> block, CtStatementList statementsToBeInserted) {
				block.insertEnd(statementsToBeInserted);
			}

			@Override
			void insertFromFirstStatement(CtBlock<?> block, CtStatement target, CtStatementList statementsToBeInserted) {
				final List<CtStatement> copy = new ArrayList<>(block.getStatements());
				int indexOfTargetElement = indexOfReference(block.getStatements(), target);
				for (CtStatement s : statementsToBeInserted) {
					copy.add(++indexOfTargetElement, s);
				}
				//remove statements from the `statementsToBeInserted` before they are added to spoon model
				//note: one element MUST NOT be part of two models.
				statementsToBeInserted.setStatements(null);
				block.setStatements(copy);
			}

			@Override
			<T extends CtElement> List<T> insertFromLastStatement(List<T> statements, CtStatement target, CtStatementList statementsToBeInserted) {
				final List<T> copy = new ArrayList<>(statements);
				int indexOfTargetElement = indexOfReference(copy, target) + 1;
				for (int j = statementsToBeInserted.getStatements().size() - 1; j >= 0; j--) {
					copy.add(indexOfTargetElement, (T) statementsToBeInserted.getStatements().get(j));
				}
				//remove statements from the `statementsToBeInserted` before they are added to spoon model
				//note: one element MUST NOT be part of two models.
				statementsToBeInserted.setStatements(null);
				return copy;
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
		abstract <T extends CtElement> List<T> insertFromLastStatement(List<T> statements, CtStatement target, CtStatementList statementsToBeInserted);
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

	@MetamodelPropertyField(role = LABEL)
	String label;

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public <T extends CtStatement> T setLabel(String label) {
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, LABEL, label, this.label);
		this.label = label;
		return (T) this;
	}

	@Override
	public CtStatement clone() {
		return (CtStatement) super.clone();
	}
}
