/**
 * Copyright (C) 2006-2016 INRIA and contributors
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
package spoon.support.compiler.jdt;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBodyHolder;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.CtInheritanceScanner;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The comment builder that will insert all element of a CompilationUnitDeclaration into the Spoon AST
 */
@SuppressWarnings("unchecked")
class JDTCommentBuilder {

	private static final Logger LOGGER = Logger.getLogger(JDTCommentBuilder.class);

	private final CompilationUnitDeclaration declarationUnit;
	private String filePath;
	private CompilationUnit spoonUnit;
	private Factory factory;
	private ICompilationUnit sourceUnit;
	private char[] contents;

	/**
	 * Creates a JDTCommentBuilder that will insert all comment of the declarationUnit into the Spoon AST
	 * @param declarationUnit the declaration unit
	 * @param factory the Spoon AST
	 */
	JDTCommentBuilder(CompilationUnitDeclaration declarationUnit,  Factory factory) {
		this.declarationUnit = declarationUnit;
		if (declarationUnit.comments == null) {
			return;
		}
		this.factory = factory;
		this.sourceUnit = declarationUnit.compilationResult.compilationUnit;
		this.contents = sourceUnit.getContents();
		this.filePath = CharOperation.charToString(sourceUnit.getFileName());
		this.spoonUnit = factory.CompilationUnit().create(filePath);
	}

	/**
	 * Start the build process
	 */
	public void build() {
		if (declarationUnit.comments == null) {
			return;
		}
		for (int j = 0; j < declarationUnit.comments.length; j++) {
			int[] positions = declarationUnit.comments[j];
			buildComment(positions);
		}
	}

	/**
	 * Inserts the comment at the position positions in the AST
	 * @param positions the position of the comment
	 */
	private void buildComment(int[] positions) {
		int start = positions[0];
		int end = -positions[1];

		CtComment comment = factory.Core().createComment();

		//
		comment.setCommentType(CtComment.CommentType.BLOCK);
		// the inline comments have negative start
		if (start < 0) {
			comment.setCommentType(CtComment.CommentType.INLINE);
			start = -start;
		}
		// Javadoc comments have negative end position
		if (end <= 0) {
			comment.setCommentType(CtComment.CommentType.JAVADOC);
			end = -end;
		}
		String commentContent = getCommentContent(start, end);

		int[] lineSeparatorPositions = declarationUnit.compilationResult.lineSeparatorPositions;
		SourcePosition sourcePosition = factory.Core().createSourcePosition(spoonUnit, start, start, end, lineSeparatorPositions);

		// create the Spoon comment element
		comment.setContent(commentContent);
		comment.setPosition(sourcePosition);

		insertCommentInAST(comment);
	}

	/**
	 * Insert the element to nearer element in the elements collections
	 * @param comment the comment to insert
	 * @param elements the collection that content the ast elements
	 * @return
	 */
	private CtElement addCommentToNear(final CtComment comment, final Collection<CtElement> elements) {
		CtElement best = null;
		int smallDistance = Integer.MAX_VALUE;
		for (CtElement element : elements) {
			if (element.getPosition() == null) {
				continue;
			}
			if (element.isImplicit()) {
				continue;
			}
			if (element instanceof CtComment) {
				continue;
			}
			final boolean isAfter = element.getPosition().getSourceEnd() < comment.getPosition().getSourceStart();
			int distance = Math.abs(element.getPosition().getSourceStart() - comment.getPosition().getSourceEnd());
			if (isAfter) {
				distance = Math.abs(element.getPosition().getSourceEnd() - comment.getPosition().getSourceStart());
			}
			if (distance < smallDistance && (!isAfter || element.getPosition().getEndLine() == comment.getPosition().getLine())) {
				best = element;
				smallDistance = distance;
			}
		}
		// adds the comment to the nearest element
		if (best != null) {
			best.addComment(comment);
		}
		return best;
	}

	/**
	 * Inserts the comment into the AST.
	 * @param comment the comment to insert
	 */
	private void insertCommentInAST(final CtComment comment) {
		CtElement commentParent = findCommentParent(comment);
		if (commentParent == null) {
			File file = spoonUnit.getFile();
			if (file != null && file.getName().equals(DefaultJavaPrettyPrinter.JAVA_PACKAGE_DECLARATION)) {
				spoonUnit.getDeclaredPackage().addComment(comment);
			} else {
				comment.setCommentType(CtComment.CommentType.FILE);
				addCommentToNear(comment, new ArrayList<CtElement>(spoonUnit.getDeclaredTypes()));
			}
			return;
		}
		// visitor that inserts the comment in the element
		CtInheritanceScanner insertionVisitor = new CtInheritanceScanner() {
			private boolean isScanned = false;
			@Override
			public void scan(CtElement e) {
				if (e == null) {
					return;
				}
				// Do not visit the AST, only the first element
				if (!isScanned) {
					isScanned = true;
					if (e.getPosition().getSourceStart() == comment.getPosition().getSourceStart()) {
						e.addComment(comment);
						return;
					}
					super.scan(e);
				}
			}

			@Override
			public <R> void visitCtStatementList(CtStatementList e) {
				addCommentToNear(comment, new ArrayList<CtElement>(e.getStatements()));
				try {
					comment.getParent();
				} catch (ParentNotInitializedException ex) {
					e.addStatement(comment);
				}
			}

			@Override
			public <T> void visitCtMethod(CtMethod<T> e) {
				e.addComment(comment);
			}

			@Override
			public <T> void visitCtConstructor(CtConstructor<T> e) {
				e.addComment(comment);
			}

			@Override
			public <T> void visitCtConditional(CtConditional<T> e) {
				List<CtElement> elements = new ArrayList<>();
				elements.add(e.getElseExpression());
				elements.add(e.getThenExpression());
				elements.add(e.getCondition());
				addCommentToNear(comment, elements);
			}

			@Override
			public <T> void visitCtBinaryOperator(CtBinaryOperator<T> e) {
				List<CtElement> elements = new ArrayList<>();
				elements.add(e.getLeftHandOperand());
				elements.add(e.getRightHandOperand());
				addCommentToNear(comment, elements);
			}

			@Override
			public <T> void visitCtClass(CtClass<T> e) {
				if (comment.getPosition().getLine() <= e.getPosition().getLine()) {
					e.addComment(comment);
					return;
				}
				final List<CtElement> elements = new ArrayList<>();
				for (CtTypeMember typeMember : e.getTypeMembers()) {
					if (typeMember instanceof CtField || typeMember instanceof CtMethod || typeMember instanceof CtConstructor) {
						elements.add(typeMember);
					}
				}
				addCommentToNear(comment, elements);

				try {
					comment.getParent();
				} catch (ParentNotInitializedException ex) {
					e.addComment(comment);
				}
			}

			@Override
			public <T> void visitCtInterface(CtInterface<T> e) {
				final List<CtElement> elements = new ArrayList<>();
				for (CtTypeMember typeMember : e.getTypeMembers()) {
					if (typeMember instanceof CtField || typeMember instanceof CtMethod) {
						elements.add(typeMember);
					}
				}
				addCommentToNear(comment, elements);

				try {
					comment.getParent();
				} catch (ParentNotInitializedException ex) {
					e.addComment(comment);
				}
			}

			@Override
			public <T> void visitCtField(CtField<T> e) {
				e.addComment(comment);
			}

			@Override
			public <E> void visitCtSwitch(CtSwitch<E> e) {
				List<CtCase<? super E>> cases = e.getCases();
				CtCase previous = null;
				for (int i = 0; i < cases.size(); i++) {
					CtCase<? super E> ctCase = cases.get(i);
					if (previous == null) {
						if (comment.getPosition().getSourceStart() < ctCase.getPosition().getSourceStart()
								&& e.getPosition().getSourceStart() < comment.getPosition().getSourceStart()) {
							ctCase.addComment(comment);
							return;
						}
					} else {
						if (previous.getPosition().getSourceEnd() < comment.getPosition().getSourceStart()
								&& ctCase.getPosition().getSourceStart() > comment.getPosition().getSourceStart()) {
							addCommentToNear(comment, new ArrayList<CtElement>(previous.getStatements()));
							try {
								comment.getParent();
							} catch (ParentNotInitializedException ex) {
								previous.addStatement(comment);
							}
							return;
						}
					}
					previous = ctCase;
				}
				if (previous.getPosition().getSourceEnd() < comment.getPosition().getSourceStart()) {
					addCommentToNear(comment, new ArrayList<CtElement>(previous.getStatements()));
					try {
						comment.getParent();
					} catch (ParentNotInitializedException ex) {
						previous.addStatement(comment);
					}
					return;
				}
				try {
					comment.getParent();
				} catch (ParentNotInitializedException ex) {
					e.addComment(comment);
				}
			}

			@Override
			public void visitCtIf(CtIf e) {
				if (!(e.getThenStatement() instanceof CtBlock)) {
					if (comment.getPosition().getSourceEnd() <= e.getThenStatement().getPosition().getSourceStart()) {
						e.getThenStatement().addComment(comment);
						return;
					}
				}
				if (e.getElseStatement() != null) {
					SourcePosition thenPosition = e.getThenStatement().getPosition() == null ? ((CtBlock) e.getThenStatement()).getStatement(0).getPosition() : e.getThenStatement().getPosition();
					SourcePosition elsePosition = e.getElseStatement().getPosition() == null ? ((CtBlock) e.getElseStatement()).getStatement(0).getPosition() : e.getElseStatement().getPosition();
					if (comment.getPosition().getSourceStart() > thenPosition.getSourceEnd() && comment.getPosition().getSourceEnd() < elsePosition.getSourceStart()) {
						e.getElseStatement().addComment(comment);
					}
				}
				try {
					comment.getParent();
				} catch (ParentNotInitializedException ex) {
					e.addComment(comment);
				}
			}

			@Override
			public void scanCtStatement(CtStatement s) {
				if (!(s instanceof CtStatementList || s instanceof CtSwitch)) {
					s.addComment(comment);
				}
			}

			@Override
			public void visitCtAnonymousExecutable(CtAnonymousExecutable e) {
				e.addComment(comment);
			}

			@Override
			public <T> void visitCtNewArray(CtNewArray<T> e) {
				addCommentToNear(comment, new ArrayList<CtElement>(e.getElements()));
				try {
					comment.getParent();
				} catch (ParentNotInitializedException ex) {
					e.addComment(comment);
				}
			}

			@Override
			public <T> void visitCtParameter(CtParameter<T> e) {
				e.addComment(comment);
			}
		};
		insertionVisitor.scan(commentParent);
		try {
			comment.getParent();
		} catch (ParentNotInitializedException e) {
			LOGGER.error(comment + " is not added into the AST", e);
		}
	}

	/**
	 * Find the parent of a comment based on the position
	 * @param comment the comment
	 * @return the parent of the comment
	 */
	private CtElement findCommentParent(CtComment comment) {
		class FindCommentParentScanner extends CtScanner {
			public CtElement commentParent;

			private int start;
			private int end;

			FindCommentParentScanner(int start, int end) {
				this.start = start;
				this.end = end;
			}

			@Override
			public void scan(CtElement element) {
				if (element == null) {
					return;
				}
				if (element.isImplicit()) {
					return;
				}
				CtElement body = getBody(element);
				if (body != null && body.getPosition() == null) {
					body = null;
				}
				if (element.getPosition() != null
						&& ((element.getPosition().getSourceStart() <= start
						&& element.getPosition().getSourceEnd() >= end)
						|| (body != null && (body.getPosition().getSourceStart() <= start
						&& body.getPosition().getSourceEnd() >= end)))) {
					commentParent = element;
					element.accept(this);
				}
			}
		}
		FindCommentParentScanner findCommentParentScanner = new FindCommentParentScanner(
				comment.getPosition().getSourceStart(),
				comment.getPosition().getSourceEnd());
		findCommentParentScanner.scan(spoonUnit.getDeclaredTypes());
		return findCommentParentScanner.commentParent;
	}

	/**
	 * @param element
	 * @return body of element or null if this element has no body
	 */
	static CtElement getBody(CtElement e) {
		if (e instanceof CtBodyHolder) {
			return ((CtBodyHolder) e).getBody();
		}
		return null;
	}

	/**
	 * Extract the comment from the content of the class
	 * @param start the start position of the comment
	 * @param end the end position of the comment
	 * @return the content of the comment
	 */
	private String getCommentContent(int start, int end) {
		//skip comment prefix
		start += 2;
		return cleanComment(new String(contents, start, end - start));
	}

	public static String cleanComment(String comment) {
		StringBuffer ret = new StringBuffer();
		String[] lines = comment.split("\n");
		// limit case
		if (lines.length == 1) {
			return lines[0].replaceAll("^/\\*+ ?", "").replaceAll("\\*+/$", "").trim();
		}

		for (String s : lines) {
			String cleanUpLine = s.trim();
			if (cleanUpLine.startsWith("/**")) {
				cleanUpLine = cleanUpLine.replaceAll("/\\*+ ?", "");
			} else if (cleanUpLine.endsWith("*/")) {
				cleanUpLine = cleanUpLine.replaceAll("\\*+/$", "").replaceAll("^[ \t]*\\*+ ?", "");
			} else {
				cleanUpLine = cleanUpLine.replaceAll("^[ \t]*\\*+ ?", "");
			}
			ret.append(cleanUpLine);
			ret.append("\n");
		}
		// clean '\r'
		StringBuffer ret2 = new StringBuffer();
		for (int i = 0; i < ret.length(); i++) {
			if (ret.charAt(i) != '\r') {
				ret2.append(ret.charAt(i));
			}
		}
		return ret2.toString().trim();
	}
}
