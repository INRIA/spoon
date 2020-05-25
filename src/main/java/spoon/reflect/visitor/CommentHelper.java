/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.code.CtJavaDocTag;
import spoon.support.Internal;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Computes source code representation of the Comment literal
 */
@Internal
public class CommentHelper {

	/**
	 * RegExp which matches all possible line separators
	 */
	private static final Pattern LINE_SEPARATORS_RE = Pattern.compile("\\n\\r|\\n|\\r");

	private CommentHelper() {
	}

	/** returns a pretty-printed version of a comment, with prefix, suffix, and intermediate prefix for block and Javadoc */
	public static String printComment(CtComment comment) {
		PrinterHelper ph = new PrinterHelper(comment.getFactory().getEnvironment());
		// now we only use one single method to print all tags
		printCommentContent(ph, comment, s -> { return  s; });
		return ph.toString();
	}


	static void printComment(PrinterHelper printer, CtComment comment) {
		CtComment.CommentType commentType = comment.getCommentType();
		String content = comment.getContent();
		// prefix
		switch (commentType) {
		case FILE:
			printer.write(DefaultJavaPrettyPrinter.JAVADOC_START).writeln();
			break;
		case JAVADOC:
			printer.write(DefaultJavaPrettyPrinter.JAVADOC_START).writeln();
			break;
		case INLINE:
			printer.write(DefaultJavaPrettyPrinter.INLINE_COMMENT_START);
			break;
		case BLOCK:
			printer.write(DefaultJavaPrettyPrinter.BLOCK_COMMENT_START);
			break;
		}
		// content
		switch (commentType) {
			case INLINE:
				printer.write(content);
				break;
			default:
				// per line suffix
				printCommentContent(printer, comment, s -> { return (" * " + s).replaceAll(" *$", ""); });
		}
		// suffix
		switch (commentType) {
			case BLOCK:
				printer.write(DefaultJavaPrettyPrinter.BLOCK_COMMENT_END);
				break;
			case FILE:
				printer.write(DefaultJavaPrettyPrinter.BLOCK_COMMENT_END);
				break;
			case JAVADOC:
				printer.write(DefaultJavaPrettyPrinter.BLOCK_COMMENT_END);
				break;
		}
	}

	static void printCommentContent(PrinterHelper printer, CtComment comment, Function<String, String> transfo) {
		CtComment.CommentType commentType = comment.getCommentType();
		String content = comment.getContent();
		String[] lines = LINE_SEPARATORS_RE.split(content);
		for (String com : lines) {
			if (commentType == CtComment.CommentType.BLOCK) {
				printer.write(com);
				if (lines.length > 1) {
					printer.write(CtComment.LINE_SEPARATOR);
				}
			} else {
				printer.write(transfo.apply(com)).writeln(); // removing spaces at the end of the space
			}
		}
		if (comment instanceof CtJavaDoc) {
			List<CtJavaDocTag> tags = null;
			Collection<CtJavaDocTag> javaDocTags = ((CtJavaDoc) comment).getTags();
			if (javaDocTags != null && javaDocTags.isEmpty() == false) {
				printer.write(transfo.apply("")).writeln();
				for (CtJavaDocTag docTag : javaDocTags) {
					printJavaDocTag(printer, docTag, transfo);
				}
			}
		}
	}

	static void printJavaDocTag(PrinterHelper printer, CtJavaDocTag docTag, Function<String, String> transfo) {
		printer.write(transfo.apply(CtJavaDocTag.JAVADOC_TAG_PREFIX));
		printer.write(docTag.getType().name().toLowerCase());
		printer.write(" ");
		if (docTag.getType().hasParam()) {
			printer.write(docTag.getParam()).writeln();
		}

		String[] tagLines = LINE_SEPARATORS_RE.split(docTag.getContent());
		for (int i = 0; i < tagLines.length; i++) {
			String com = tagLines[i];
			if (docTag.getType().hasParam()) {
				printer.write(transfo.apply("\t\t"));
			}
			printer.write(com.trim()).writeln();
		}
	}
}
