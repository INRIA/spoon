/**
 * Copyright (C) 2006-2018 INRIA and contributors
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

import spoon.SpoonException;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBodyHolder;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.code.CtJavaDocTag;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.cu.position.BodyHolderSourcePosition;
import spoon.reflect.cu.position.DeclarationSourcePosition;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.CtInheritanceScanner;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * The comment builder that will insert all element of a CompilationUnitDeclaration into the Spoon AST
 */
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
		this.spoonUnit = factory.CompilationUnit().getOrCreate(filePath);
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

		CtComment comment;

		// Javadoc comments have negative end position
		if (end <= 0) {
			comment = factory.Core().createJavaDoc();
			end = -end;
		} else {
			comment = factory.Core().createComment();
			comment.setCommentType(CtComment.CommentType.BLOCK);

			// the inline comments have negative start
			if (start < 0) {
				comment.setCommentType(CtComment.CommentType.INLINE);
				start = -start;
			}
		}

		/**
		 * Comment, which contains only javadoc tags never set content.
		 * So set content now, to avoid unexpected null content.
		 */
		comment.setContent("");

		String commentContent = getCommentContent(start, end);

		int[] lineSeparatorPositions = declarationUnit.compilationResult.lineSeparatorPositions;
		SourcePosition sourcePosition = factory.Core().createSourcePosition(spoonUnit, start, end - 1, lineSeparatorPositions);

		// create the Spoon comment element
		comment = parseTags(comment, commentContent);
		comment.setPosition(sourcePosition);

		insertCommentInAST(comment);
	}

	/**
	 * Parse the content of a comment to extract the tags
	 * @param comment the original comment
	 * @param commentContent the content of the comment
	 * @return a CtComment or a CtJavaDoc comment with a defined content
	 */
	private CtComment parseTags(CtComment comment, String commentContent) {
		if (!(comment instanceof CtJavaDoc)) {
			comment.setContent(commentContent);
			return comment;
		}

		String currentTagContent = "";
		CtJavaDocTag.TagType currentTag = null;

		// TODO: remove the " *", see spoon.test.javadoc.JavaDocTest.testJavaDocReprint()
		String[] lines = commentContent.split("\n");
		for (String aLine : lines) {
			String line = aLine.trim();
			if (line.startsWith(CtJavaDocTag.JAVADOC_TAG_PREFIX)) {
				int endIndex = line.indexOf(' ');
				if (endIndex == -1) {
					endIndex = line.length();
				}
				defineCommentContent(comment, currentTagContent, currentTag);

				currentTag = CtJavaDocTag.TagType.tagFromName(line.substring(1, endIndex).toLowerCase());
				if (endIndex == line.length()) {
					currentTagContent = "";
				} else {
					currentTagContent = line.substring(endIndex + 1);
				}
			} else {
				currentTagContent += "\n" + aLine;
			}
		}
		defineCommentContent(comment, currentTagContent, currentTag);
		return comment;
	}

	/**
	 * Define the content of the comment
	 * @param comment the comment
	 * @param tagContent the tagContent of the tag
	 * @param tagType the tag type
	 */
	private void defineCommentContent(CtComment comment, String tagContent, CtJavaDocTag.TagType tagType) {
		if (tagType != null) {
			CtJavaDocTag docTag = comment.getFactory().Code().createJavaDocTag(tagContent, tagType);
			((CtJavaDoc) comment).addTag(docTag);
		} else if (!tagContent.isEmpty()) {
			comment.setContent(tagContent.trim());
		}
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
			if (element.getPosition().isValidPosition() == false) {
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

			int elementEndLine = element.getPosition().getEndLine();
			int commentLine = comment.getPosition().getLine();

			if (distance < smallDistance && (!isAfter || elementEndLine == commentLine || element instanceof CtType)) {
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
			} else if (file != null && file.getName().equals(DefaultJavaPrettyPrinter.JAVA_MODULE_DECLARATION)) {
				spoonUnit.getDeclaredModule().addComment(comment);
			} else {
				comment.setCommentType(CtComment.CommentType.FILE);
				addCommentToNear(comment, new ArrayList<>(spoonUnit.getDeclaredTypes()));
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
					SourcePosition sp = e.getPosition();
					if (sp.getSourceStart() == comment.getPosition().getSourceStart()) {
						e.addComment(comment);
						return;
					}
					if (sp instanceof DeclarationSourcePosition) {
						DeclarationSourcePosition dsp = (DeclarationSourcePosition) sp;
						if (comment.getPosition().getSourceEnd() < dsp.getModifierSourceEnd()) {
							e.addComment(comment);
							return;
						}
					}
					super.scan(e);
				}
			}

			@Override
			public void scanCtReference(CtReference reference) {
				reference.addComment(comment);
				super.scanCtReference(reference);
			}

			@Override
			public <R> void visitCtStatementList(CtStatementList e) {
				addCommentToNear(comment, new ArrayList<>(e.getStatements()));
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
				//all the comments located before the body brackets belongs to class
				if (comment.getPosition().getSourceEnd() < ((BodyHolderSourcePosition) e.getPosition()).getBodyStart()) {
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
			public <T> void scanCtVariable(CtVariable<T> e) {
				e.addComment(comment);
			}

			@Override
			public <E> void visitCtSwitch(CtSwitch<E> e) {
				List<CtCase<? super E>> cases = e.getCases();
				CtCase previous = null;
				for (CtCase<? super E> ctCase : cases) {
					if (previous == null) {
						if (comment.getPosition().getSourceStart() < ctCase.getPosition().getSourceStart()
								&& e.getPosition().getSourceStart() < comment.getPosition().getSourceStart()) {
							ctCase.addComment(comment);
							return;
						}
					} else {
						if (previous.getPosition().getSourceEnd() < comment.getPosition().getSourceStart()
								&& ctCase.getPosition().getSourceStart() > comment.getPosition().getSourceStart()) {
							addCommentToNear(comment, new ArrayList<>(previous.getStatements()));
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
					addCommentToNear(comment, new ArrayList<>(previous.getStatements()));
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
					SourcePosition thenPosition = e.getThenStatement().getPosition().isValidPosition() == false ? ((CtBlock) e.getThenStatement()).getStatement(0).getPosition() : e.getThenStatement().getPosition();
					SourcePosition elsePosition = e.getElseStatement().getPosition().isValidPosition() == false ? ((CtBlock) e.getElseStatement()).getStatement(0).getPosition() : e.getElseStatement().getPosition();
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
				if (!(s instanceof CtStatementList || s instanceof CtSwitch || s instanceof CtVariable)) {
					s.addComment(comment);
				}
			}

			@Override
			public void visitCtAnonymousExecutable(CtAnonymousExecutable e) {
				e.addComment(comment);
			}

			@Override
			public <T> void visitCtNewArray(CtNewArray<T> e) {
				addCommentToNear(comment, new ArrayList<>(e.getElements()));
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

			@Override
			public void visitCtCatch(CtCatch e) {
				if (comment.getPosition().getLine() <= e.getPosition().getLine()) {
					e.addComment(comment);
				}
			}

			@Override
			public void visitCtModule(CtModule module) {
				addCommentToNear(comment, new ArrayList<>(module.getModuleDirectives()));
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

			private boolean isCommentBetweenElementPosition(CtElement element) {
				return (element.getPosition().isValidPosition()
						&& element.getPosition().getSourceStart() <= this.start
						&& element.getPosition().getSourceEnd() >= this.end);
			}

			@Override
			public void scan(CtElement element) {
				if (element == null) {
					return;
				}
				if (element.isImplicit() && !(element instanceof CtBlock)) {
					return;
				}
				CtElement body = getBody(element);
				if (body != null && body.getPosition().isValidPosition() == false) {
					body = null;
				}

				boolean betweenElementPosition = this.isCommentBetweenElementPosition(element);
				boolean bodyBetweenElementPosition = (body != null) && this.isCommentBetweenElementPosition(body);

				if (betweenElementPosition || bodyBetweenElementPosition) {
					commentParent = element;
					element.accept(this);
				}
			}
		}
		FindCommentParentScanner findCommentParentScanner = new FindCommentParentScanner(
				comment.getPosition().getSourceStart(),
				comment.getPosition().getSourceEnd());

		if (!spoonUnit.getDeclaredTypes().isEmpty()) {
			findCommentParentScanner.scan(spoonUnit.getDeclaredTypes());
		} else if (spoonUnit.getDeclaredModule() != null) {
			findCommentParentScanner.scan(spoonUnit.getDeclaredModule());
		}

		return findCommentParentScanner.commentParent;
	}

	/**
	 * @param e
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
		return cleanComment(new CharArrayReader(contents, start, end - start));
	}

	public static String cleanComment(String comment) {
		return cleanComment(new StringReader(comment));
	}

	private static final Pattern startCommentRE = Pattern.compile("^/\\*{1,2} ?");
	private static final Pattern middleCommentRE = Pattern.compile("^[ \t]*\\*? ?");
	private static final Pattern endCommentRE = Pattern.compile("\\*/$");

	private static String cleanComment(Reader comment) {
		StringBuilder ret = new StringBuilder();
		try (BufferedReader br = new BufferedReader(comment)) {
			String line = br.readLine();
			if (line.length() < 2 || line.charAt(0) != '/') {
				throw new SpoonException("Unexpected beginning of comment");
			}
			boolean isLastLine = false;
			if (line.charAt(1) == '/') {
				//it is single line comment, which starts with "//"
				isLastLine = true;
				line = line.substring(2);
			} else {
				//it is potentially multiline comment, which starts with "/*" or "/**"
				//check end first
				if (line.endsWith("*/") && line.length() > 3) {
					//it is last line
					line = endCommentRE.matcher(line).replaceFirst("");
					isLastLine = true;
				}
				//skip beginning
				line = startCommentRE.matcher(line).replaceFirst("");
			}
			//append first line
			ret.append(line);
			while ((line = br.readLine()) != null) {
				if (isLastLine) {
					throw new SpoonException("Unexpected next line after last line");
				}
				if (line.endsWith("*/")) {
					//it is last line
					line = endCommentRE.matcher(line).replaceFirst("");
					isLastLine = true;
				}
				//always clean middle comment, but after end comment is detected
				line = middleCommentRE.matcher(line).replaceFirst("");
				//write next line - Note that Spoon model comment's lines are always separated by "\n"
				ret.append(CtComment.LINE_SEPARATOR);
				ret.append(line);
			}
			return ret.toString().trim();
		} catch (IOException e) {
			throw new SpoonException(e);
		}
	}
}
