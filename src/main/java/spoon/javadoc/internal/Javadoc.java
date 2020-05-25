/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
/**
 *  This file originally comes from JavaParser and is distributed under the terms of
 * a) the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * b) the terms of the Apache License
 */
package spoon.javadoc.internal;

import spoon.reflect.code.CtComment;

import static spoon.javadoc.internal.JavadocInlineTag.nextWord;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
* The structured content of a single Javadoc comment.
*
* <p>It is composed by a description and a list of block tags.
*
* <p>An example would be the text contained in this very Javadoc comment. At the moment of this
* writing this comment does not contain any block tags (such as <code>@see AnotherClass</code>)
*/
public class Javadoc implements Serializable {

	private JavadocDescription description;
	private List<JavadocBlockTag> blockTags;

	public Javadoc() {
		this(new JavadocDescription());
	}

	public Javadoc(JavadocDescription description) {
		this.description = description;
		this.blockTags = new LinkedList<>();
	}

	public Javadoc addBlockTag(JavadocBlockTag blockTag) {
		this.blockTags.add(blockTag);
		return this;
	}

	/** For tags like "@return good things" where tagName is "return", and the rest is content. */
	public Javadoc addBlockTag(String tagName, String parameter, String content) {
		return addBlockTag(tagName, parameter, content);
	}

	public Javadoc addBlockTag(String tagName) {
		return addBlockTag(tagName, "", "");
	}

	/**
		* Return the text content of the document. It does not containing trailing spaces and asterisks
		* at the start of the line.
		*/
	public String toText() {
		StringBuilder sb = new StringBuilder();
		if (!description.isEmpty()) {
			sb.append(description.toText());
			sb.append(System.lineSeparator());
		}
		if (!blockTags.isEmpty()) {
			sb.append(System.lineSeparator());
		}
		blockTags.forEach(
			bt -> {
				sb.append(bt.toText());
				sb.append(System.lineSeparator());
			});
		return sb.toString();
	}

	public JavadocDescription getDescription() {
		return description;
	}

	/** @return the current List of associated JavadocBlockTags */
	public List<JavadocBlockTag> getBlockTags() {
		return this.blockTags;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Javadoc document = (Javadoc) o;

		return description.equals(document.description) && blockTags.equals(document.blockTags);
	}

	@Override
	public int hashCode() {
		int result = description.hashCode();
		result = 31 * result + blockTags.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "Javadoc{" + "description=" + description + ", blockTags=" + blockTags + '}';
	}

	private static String BLOCK_TAG_PREFIX = "@";
	private static Pattern BLOCK_PATTERN =
			Pattern.compile("^\\s*" + BLOCK_TAG_PREFIX, Pattern.MULTILINE);

	/** parse the description part (before tags) of a Javadoc */
	public static JavadocDescription parseText(String text) {
		JavadocDescription instance = new JavadocDescription();
		int index = 0;
		Pair<Integer, Integer> nextInlineTagPos;
		while ((nextInlineTagPos = indexOfNextInlineTag(text, index)) != null) {
			if (nextInlineTagPos.a != index) {
			instance.addElement(new JavadocSnippet(text.substring(index, nextInlineTagPos.a)));
			}
			instance.addElement(
				JavadocInlineTag.fromText(text.substring(nextInlineTagPos.a, nextInlineTagPos.b + 1)));
			index = nextInlineTagPos.b + 1;
		}
		if (index < text.length()) {
			instance.addElement(new JavadocSnippet(text.substring(index)));
		}
		return instance;
	}

	private static Pair<Integer, Integer> indexOfNextInlineTag(String text, int start) {
		int index = text.indexOf("{@", start);
		if (index == -1) {
			return null;
		}
		// we are interested only in complete inline tags
		int closeIndex = text.indexOf("}", index);
		if (closeIndex == -1) {
			return null;
		}
		return new Pair<>(index, closeIndex);
	}

	/** parses the Javadoc content (description + tags) */
	public static Javadoc parse(String commentContent) {
		List<String> cleanLines;
		cleanLines = Arrays.asList(commentContent.split(CtComment.LINE_SEPARATOR));
		int indexOfFirstBlockTag =
			cleanLines
					.stream()
					.filter(Javadoc::isABlockLine)
					.map(cleanLines::indexOf)
					.findFirst()
					.orElse(-1);
		List<String> blockLines;
		String descriptionText;
		if (indexOfFirstBlockTag == -1) {
			descriptionText = trimRight(String.join(CtComment.LINE_SEPARATOR, cleanLines));
			blockLines = Collections.emptyList();
		} else {
			descriptionText = trimRight(String.join(CtComment.LINE_SEPARATOR, cleanLines.subList(0, indexOfFirstBlockTag)));

			// Combine cleaned lines, but only starting with the first block tag till the end
			// In this combined string it is easier to handle multiple lines which actually belong
			// together
			String tagBlock =
				cleanLines
					.subList(indexOfFirstBlockTag, cleanLines.size())
					.stream()
					.collect(Collectors.joining(CtComment.LINE_SEPARATOR));

			// Split up the entire tag back again, considering now that some lines belong to the
			// same
			// block tag.
			// The pattern splits the block at each new line starting with the '@' symbol, thus the
			// symbol
			// then needs to be added again so that the block parsers handles everything correctly.
			blockLines =
				BLOCK_PATTERN
					.splitAsStream(tagBlock)
					.filter(x -> !x.isEmpty())
					.map(s -> BLOCK_TAG_PREFIX + s)
					.collect(Collectors.toList());
		}
		Javadoc document = new Javadoc(parseText(descriptionText));
		blockLines.forEach(l -> document.addBlockTag(parseBlockTag(l)));
		return document;
	}

	private static JavadocBlockTag parseBlockTag(String line) {
		line = line.trim().substring(1);
		String tagName = nextWord(line);
		String rest = line.substring(tagName.length()).trim();
		return new JavadocBlockTag(tagName, rest);
	}

	private static boolean isABlockLine(String line) {
		return line.trim().startsWith(BLOCK_TAG_PREFIX);
	}

	private static String trimRight(String string) {
		while (!string.isEmpty() && Character.isWhitespace(string.charAt(string.length() - 1))) {
			string = string.substring(0, string.length() - 1);
		}
		return string;
	}

}
