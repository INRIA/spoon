/**
 * SPDX-License-Identifier: (MIT OR CECILL-C)
 *
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.cu.position;

import spoon.SpoonException;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.cu.position.BodyHolderSourcePosition;
import spoon.reflect.cu.position.DeclarationSourcePosition;
import spoon.reflect.cu.position.NoSourcePosition;

import java.io.File;
import java.io.Serializable;

/**
 * This immutable class represents the position of a Java program element in a source
 * file.
 */
public class SourcePositionImpl implements SourcePosition, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Search the line number corresponding to a specific position
	 */
	protected int searchLineNumber(int position) {
		int[] lineSeparatorPositions = getLineSeparatorPositions();
		if (lineSeparatorPositions == null) {
			return 1;
		}
		int length = lineSeparatorPositions.length;
		if (length == 0) {
			return 1;
		}
		int g = 0;
		int d = length - 1;
		int m = 0;
		int start;
		while (g <= d) {
			m = (g + d) / 2;
			if (position < (start = lineSeparatorPositions[m])) {
				d = m - 1;
			} else if (position > start) {
				g = m + 1;
			} else {
				return m + 1;
			}
		}
		if (position < lineSeparatorPositions[m]) {
			return m + 1;
		}
		return m + 2;
	}

	/**
	 * Search the column number
	 */
	protected int searchColumnNumber(int position) {
		int[] lineSeparatorPositions = getLineSeparatorPositions();
		if (lineSeparatorPositions == null) {
			return -1;
		}
		int length = lineSeparatorPositions.length;
		if (length == 0) {
			return position;
		}
		if (lineSeparatorPositions[0] > position) {
			return position;
		}
		int i;
		for (i = 0; i < lineSeparatorPositions.length - 1; i++) {
			if (lineSeparatorPositions[i] < position && (lineSeparatorPositions[i + 1] > position)) {
				return position - lineSeparatorPositions[i];
			}
		}
		int tabCount = 0;
		int tabSize = 0;
		if (getCompilationUnit() != null) {
			tabSize = getCompilationUnit().getFactory().getEnvironment().getTabulationSize();
			String source = getCompilationUnit().getOriginalSourceCode();
			for (int j = lineSeparatorPositions[i]; j < position; j++) {
				if (source.charAt(j) == '\t') {
					tabCount++;
				}
			}
		}
		return (position - lineSeparatorPositions[i]) - tabCount + (tabCount * tabSize);
	}

	/** The position of the first byte of this element (incl. documentation and modifiers) */
	private final int sourceStart;

	/** The position of the last byte of this element */
	private final int sourceEnd;

	/** The line number of the start of the element, if appropriate (eg the method name).
	 * Computed lazily by {@link #getLine()}
	 */
	private int sourceStartline = -1;

	public SourcePositionImpl(CompilationUnit compilationUnit, int sourceStart, int sourceEnd, int[] lineSeparatorPositions) {
		checkArgsAreAscending(sourceStart, sourceEnd + 1);
		if (compilationUnit == null) {
			throw new SpoonException("Mandatory parameter compilationUnit is null");
		}
		this.compilationUnit = compilationUnit;
		//TODD: this check will be removed after we remove lineSeparatorPositions from the Constructor
		if (compilationUnit.getLineSeparatorPositions() != lineSeparatorPositions) {
			throw new SpoonException("Unexpected lineSeparatorPositions");
		}
		this.sourceEnd = sourceEnd;
		this.sourceStart = sourceStart;
	}

	@Override
	public boolean isValidPosition() {
		return true;
	}

	@Override
	public int getColumn() {
		return searchColumnNumber(sourceStart);
	}

	@Override
	public int getEndColumn() {
		return searchColumnNumber(sourceEnd);
	}

	@Override
	public File getFile() {
		return compilationUnit == null ? null : compilationUnit.getFile();
	}

	@Override
	public int getLine() {
		if (sourceStartline == -1) {
			this.sourceStartline = searchLineNumber(this.sourceStart);
		}
		return sourceStartline;
	}

	@Override
	public int getEndLine() {
		return searchLineNumber(sourceEnd);
	}

	@Override
	public int getSourceEnd() {
		return this.sourceEnd;
	}

	@Override
	public int getSourceStart() {
		return this.sourceStart;
	}

	/**
	 * Returns a string representation of this position in the form
	 * "sourcefile:line", or "sourcefile" if no line number is available.
	 */
	@Override
	public String toString() {
		if (getFile() == null) {
			return "(unknown file)";
		}
		int ln = getLine();
		return (ln >= 1) ? "(" + getFile().getAbsolutePath().replace('\\', '/').replace("C:/", "/") + ":" + ln + ")" : getFile().getAbsolutePath().replace('\\', '/').replace("C:/", "/");
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SourcePosition)) {
			return false;
		}
		if (obj instanceof NoSourcePosition) {
			return false;
		}
		SourcePosition s = (SourcePosition) obj;
		return (getFile() == null ? s.getFile() == null : getFile().equals(s.getFile())) && getSourceEnd() == s.getSourceEnd() && getSourceStart() == s.getSourceStart();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getLine();
		result = prime * result + getColumn();
		result = prime * result + (getFile() != null ? getFile().hashCode() : 1);
		return result;
	}

	private final CompilationUnit compilationUnit;

	@Override
	public CompilationUnit getCompilationUnit() {
		return compilationUnit;
	}

	/**
	 * Helper for debugging purposes. Displays |startIndex; endIndex|sourceCode| of this {@link SourcePosition}
	 * If this instance is {@link DeclarationSourcePosition} or {@link BodyHolderSourcePosition}
	 * Then details about name, modifiers and body are included in resulting string too
	 * @return details about source code of this {@link SourcePosition}
	 */
	public String getSourceDetails() {
		return getFragment(getSourceStart(), getSourceEnd());
	}

	protected String getFragment(int start, int end) {
		return "|" + start + ";" + end + "|" + getCompilationUnit().getOriginalSourceCode().substring(start, end + 1) + "|";
	}

	/**
	 * fails when `values` are not sorted ascending
	 * It is used to check whether start/end values of SourcePosition are consistent
	 */
	protected static void checkArgsAreAscending(int...values) {
		int last = -1;
		for (int value : values) {
			if (value < 0) {
				throw new SpoonException("SourcePosition value must not be negative");
			}
			if (last > value) {
				throw new SpoonException("SourcePosition values must be ascending or equal");
			}
			last = value;
		}
	}

	private int[] getLineSeparatorPositions() {
		return compilationUnit == null ? null : compilationUnit.getLineSeparatorPositions();
	}
}
