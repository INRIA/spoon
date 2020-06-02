/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.compiler.jdt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import spoon.SpoonException;
import spoon.reflect.code.CtAbstractSwitch;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtBodyHolder;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtStatementList;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSwitchExpression;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.cu.position.BodyHolderSourcePosition;
import spoon.reflect.cu.position.DeclarationSourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtAnonymousExecutable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModule;
import spoon.reflect.declaration.CtPackageDeclaration;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.CtInheritanceScanner;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.EarlyTerminatingScanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * The comment builder that will insert all element of a CompilationUnitDeclaration into the Spoon AST
 */
public class JDTCommentBuilder {

	private static final Logger LOGGER = LogManager.getLogger();

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
		this.spoonUnit = JDTTreeBuilder.getOrCreateCompilationUnit(declarationUnit, factory);
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

		comment.setContent(getCommentContent(start, end));

		// set the position
		int[] lineSeparatorPositions = declarationUnit.compilationResult.lineSeparatorPositions;
		SourcePosition sourcePosition = factory.Core().createSourcePosition(spoonUnit, start, end - 1, lineSeparatorPositions);
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
		if (commentParent instanceof CtPackageDeclaration || commentParent instanceof CtCompilationUnit) {
			File file = spoonUnit.getFile();
			if (file == null) {
				//it is a virtual compilation unit - e.g. Snipet compilation unit
				//all such comments belongs to declared type
				List<CtType<?>> types = spoonUnit.getDeclaredTypes();
				if (types.size() > 0) {
					types.get(0).addComment(comment);
					return;
				}
			} else if (file.getName().equals(DefaultJavaPrettyPrinter.JAVA_PACKAGE_DECLARATION)) {
				//all compilation unit comments and package declaration comments are in package
				//other comments can belong to imports or types declared in package-info.java file
				spoonUnit.getDeclaredPackage().addComment(comment);
				return;
			} else if (file.getName().equals(DefaultJavaPrettyPrinter.JAVA_MODULE_DECLARATION)) {
				spoonUnit.getDeclaredModule().addComment(comment);
				return;
			}
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

			private <T> void visitInterfaceType(CtType<T> e) {
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
			public <T> void visitCtInterface(CtInterface<T> e) {
				visitInterfaceType(e);
			}

			@Override
			public <A extends Annotation> void visitCtAnnotationType(CtAnnotationType<A> e) {
				visitInterfaceType(e);
			}

			@Override
			public void visitCtCompilationUnit(CtCompilationUnit compilationUnit) {
				compilationUnit.addComment(comment);
			}

			@Override
			public void visitCtPackageDeclaration(CtPackageDeclaration packageDeclaration) {
				packageDeclaration.addComment(comment);
			}

			@Override
			public void visitCtImport(CtImport ctImport) {
				ctImport.addComment(comment);
			}

			@Override
			public <T> void scanCtVariable(CtVariable<T> e) {
				e.addComment(comment);
			}

			private <S> void visitSwitch(CtAbstractSwitch<S> e) {
				List<CtCase<? super S>> cases = e.getCases();
				CtCase previous = null;
				for (CtCase<? super S> ctCase : cases) {
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
				if (previous != null && previous.getPosition().getSourceEnd() < comment.getPosition().getSourceStart()) {
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
			public <E> void visitCtSwitch(CtSwitch<E> e) {
				visitSwitch(e);
			}

			@Override
			public <T, S> void visitCtSwitchExpression(CtSwitchExpression<T, S> e) {
				visitSwitch(e);
			}

			@Override
			public void visitCtIf(CtIf e) {
				CtStatement thenStatement = e.getThenStatement();
				if (thenStatement != null) {
					if (!(thenStatement instanceof CtBlock)) {
						if (comment.getPosition().getSourceEnd() <= thenStatement.getPosition().getSourceStart()) {
							thenStatement.addComment(comment);
							return;
						}
					}
				}
				CtStatement elseStatement = e.getElseStatement();
				if (elseStatement != null && thenStatement != null) {
					SourcePosition thenPosition = thenStatement.getPosition();
					if (!thenPosition.isValidPosition() && thenStatement instanceof CtBlock) {
						CtStatement thenExpression = ((CtBlock) thenStatement).getStatement(0);
						thenPosition = thenExpression.getPosition();
					}
					SourcePosition elsePosition = elseStatement.getPosition();
					if (!elsePosition.isValidPosition() && elseStatement instanceof CtBlock) {
						CtStatement elseExpression = ((CtBlock) elseStatement).getStatement(0);
						elsePosition = elseExpression.getPosition();
					}
					if (comment.getPosition().getSourceStart() > thenPosition.getSourceEnd() && comment.getPosition().getSourceEnd() < elsePosition.getSourceStart()) {
						elseStatement.addComment(comment);
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
			public <T> void visitCtLambda(CtLambda<T> e) {
				if (e.getExpression() != null) {
					CtParameter<?> lastParameter = e.getParameters().get(e.getParameters().size() - 1);
					if (comment.getPosition().getSourceStart() > lastParameter.getPosition().getSourceEnd()) {
						e.getExpression().addComment(comment);
					} else {
						e.addComment(comment);
					}
				} else if (e.getBody() != null) {
					e.addComment(comment);
				}
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
				} else {
					e.getBody().addComment(comment);
				}
			}

			@Override
			public void visitCtModule(CtModule module) {
				addCommentToNear(comment, new ArrayList<>(module.getModuleDirectives()));
			}

			@Override
			public <A extends Annotation> void visitCtAnnotation(CtAnnotation<A> e) {
				addCommentToNear(comment, new ArrayList<>(e.getValues().values()));
			}
		};
		insertionVisitor.scan(commentParent);

		// postcondition
		// now we make sure that there is a parent
		// if there is no parent
		if (!comment.isParentInitialized()) {
			// that's a serious error, there is something to debug
			LOGGER.error("\"" + comment + "\" cannot be added into the AST, with parent " + commentParent.getClass()
					+ " at " + commentParent.getPosition().toString()
					+ ", please report the bug by posting on https://github.com/INRIA/spoon/issues/2482");
		}
	}

	/**
	 * Find the parent of a comment based on the position
	 * @param comment the comment
	 * @return the parent of the comment
	 */
	private CtElement findCommentParent(CtComment comment) {
		class FindCommentParentScanner extends EarlyTerminatingScanner<Void> {
			public CtElement commentParent;

			private int start;
			private int end;

			FindCommentParentScanner(int start, int end) {
				this.start = start;
				this.end = end;
				setVisitCompilationUnitContent(true);
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

		findCommentParentScanner.scan(spoonUnit);

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
		return new String(contents, start, end - start);
	}

	public static String cleanComment(String comment) {
		if (comment == null) {
			return "";
		}
		return cleanComment(new StringReader(comment));
	}

	private static final Pattern startCommentRE = Pattern.compile("^/\\*{1,2} ?");
	private static final Pattern middleCommentRE = Pattern.compile("^[ \t]*\\*? ?");
	private static final Pattern endCommentRE = Pattern.compile("\\*/$");

	private static String cleanComment(Reader comment) {
		StringBuilder ret = new StringBuilder();
		try (BufferedReader br = new BufferedReader(comment)) {
			String line = br.readLine();
			// nothing in the first line
			if (line == null) {
				return ret.toString();
			}
			boolean isLastLine = false;
			if (line.length() >= 2 && line.charAt(1) == '/') {
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
