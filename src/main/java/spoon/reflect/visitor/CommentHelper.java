/*
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2023 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) or the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.reflect.visitor;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.code.CtJavaDocTag;
import spoon.support.Internal;

/**
 * Computes source code representation of the Comment literal
 */
@Internal
public class CommentHelper {


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
		case MARKDOWN:
			// no block prefix; each line gets "/// " from the content handler
			break;
		case BLOCK:
			String commentStart = DefaultJavaPrettyPrinter.BLOCK_COMMENT_START;
			if (printer.prefixBlockComments) {
				commentStart = commentStart.stripTrailing();
			}
			printer.write(commentStart);
			if (printer.prefixBlockComments) {
				printer.writeln();
			}
			break;
		}
		// content
		switch (commentType) {
			case INLINE -> printer.write(content);
			case MARKDOWN -> {
				// Each line of a markdown comment gets a "/// " prefix.
				// Lines are separated by writeln() so the output is one "///" line per logical line.
				// Empty lines use "///" without a trailing space to stay idiomatic.
				String[] mdLines = content.lines().toArray(String[]::new);
				for (int i = 0; i < mdLines.length; i++) {
					if (i > 0) {
						printer.writeln();
					}
					String mdLine = mdLines[i];
					printer.write(mdLine.isEmpty() ? "///" : DefaultJavaPrettyPrinter.MARKDOWN_COMMENT_START + mdLine);
				}
			}
			case FILE, BLOCK -> {
				UnaryOperator<String> op;
				if (printer.prefixBlockComments) {
					op = s -> s.isEmpty() ? " *" : " * " + s;
				} else {
					op = s -> s;
				}
				printCommentContent(printer, comment, op);
			}
			case JAVADOC ->
				// per line suffix
					printCommentContent(printer, comment, s -> (" * " + s).replaceAll(" *$", ""));
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
			// INLINE and MARKDOWN have no suffix
		}
	}

	static void printCommentContent(PrinterHelper printer, CtComment comment, Function<String, String> transfo) {
		CtComment.CommentType commentType = comment.getCommentType();
		String content = comment.getContent();

		content.lines().forEach(line -> {
			if (commentType == CtComment.CommentType.BLOCK) {
				printer.write(transfo.apply(line));
				if (hasMoreThanOneElement(content.lines())) {
					printer.writeln();
				}
			} else {
				printer.write(transfo.apply(line)).writeln(); // removing spaces at the end of the space
			}
		});
		if (comment instanceof CtJavaDoc) {
			Collection<CtJavaDocTag> javaDocTags = ((CtJavaDoc) comment).getTags();
			if (javaDocTags != null && !javaDocTags.isEmpty()) {
				printer.write(transfo.apply("")).writeln();
				for (CtJavaDocTag docTag : javaDocTags) {
					printJavaDocTag(printer, docTag, transfo);
				}
			}
		}
	}

	/**
	 * Checks if the given stream has more than one element.
	 * @param stream  the stream to check
	 * @return  true if the stream has more than one element, false otherwise.
	 */
	private static boolean hasMoreThanOneElement(Stream<?> stream) {
		return stream.skip(1).findAny().isPresent();
	}

	static void printJavaDocTag(PrinterHelper printer, CtJavaDocTag docTag, Function<String, String> transfo) {
		printer.write(transfo.apply(CtJavaDocTag.JAVADOC_TAG_PREFIX));
		printer.write(CtJavaDocTag.TagType.UNKNOWN.getName().equalsIgnoreCase(docTag.getType().name()) ? docTag.getRealName() : docTag.getType().getName().toLowerCase());
		printer.write(" ");
		if (docTag.getType().hasParam()) {
			printer.write(docTag.getParam()).writeln();
		}

		docTag.getContent().lines().forEach(com -> {
			if (docTag.getType().hasParam()) {
				printer.write(transfo.apply("\t\t"));
			}
			printer.write(com.trim()).writeln();
		});
	}
}
