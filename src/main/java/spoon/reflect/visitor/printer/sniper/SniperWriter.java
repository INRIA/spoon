/**
 * Copyright (C) 2006-2017 INRIA and contributors
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
package spoon.reflect.visitor.printer.sniper;

import spoon.compiler.Environment;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtStatement;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SniperWriter {
	private StringBuilder content = new StringBuilder();
	/**
	 * Offset of print
	 */
	private Map<Integer, Integer> offset;
	private String classContent;
	private DefaultJavaPrettyPrinter printer;
	private List<String> imports;

	SniperWriter(String content, Environment env) {
		classContent = content;
		this.content.append(content);
		offset = new HashMap<>();
		printer = new DefaultJavaPrettyPrinter(env);
		imports = getImports(content);
	}

	public SniperWriter replaceModifiers(int start, int end, CtModifiable element) {
		printer.getElementPrinterHelper().writeModifiers(element);
		String content = printer.getResult();
		printer.reset();

		return this.replace(start, end, content.trim());
	}
	public SniperWriter replace(int start, int end, String content) {
		start = getPosition(start);
		end = getPosition(end);

		this.content.delete(start, end + 1);
		this.content.insert(start,  content);
		addOffset(start, content.length() - (end + 1 - start));
		return this;
	}

	public SniperWriter write(CtElement element, int position) {
		return write(element, position, false);
	}

	public SniperWriter write(CtElement element, int position, boolean isNewLine) {
		int realPosition = getPosition(position);
		if (isNewLine) {
			realPosition += getPositionNewLine(position);
		}

		printer.getPrinterHelper().setTabCount(getIndentation(realPosition));
		printer.getPrinterHelper().writeTabs();
		Collection<CtReference> imports = printer.computeImports(element.getParent(CtType.class));
		printer.scan(element);
		String content = printer.getResult().replaceAll("\\s+$", "");
		printer.reset();


		if (element instanceof CtComment) {
			content += "\n";
		} else if (element instanceof CtStatement && !content.endsWith("\n") && this.content.charAt(realPosition + 1) != '\n') {
			content += ";\n";
		} else if (element instanceof CtAnnotation) {
			content += "\n";
		}

		element.setPosition(element.getFactory().createSourcePosition(null, position, position, null));
		write(content, position, isNewLine);

		Set<String> missingImports = computeMissingImports(imports);
		printImports(missingImports);

		return this;
	}

	public SniperWriter write(String content, int position, boolean isNewLine) {
		int realPosition = getPosition(position);
		if (isNewLine) {
			realPosition += getPositionNewLine(position);
		}
		this.content.insert(realPosition, content);
		addOffset(realPosition, content.length());
		return this;
	}

	public SniperWriter remove(CtElement element) {
		SourcePosition position = element.getPosition();
		if (position == null) {
			return this;
		}
		int start = getPosition(element.getPosition().getSourceStart());
		int oldStart = start;
		while (content.charAt(start - 1) == ' ' || content.charAt(start - 1) == '\t') {
			start--;
		}
		if (content.charAt(start - 1) != '\n') {
			start = oldStart;
		}
		int end = getPosition(element.getPosition().getSourceEnd());
		if (content.charAt(end + 1) == '\n') {
			end++;
		}
		this.content.delete(start, end + 1);
		addOffset(start, start - end - 1);
		return this;
	}

	public SniperWriter remove(int start, int end) {
		start = getPosition(start);
		end = getPosition(end);
		this.content.delete(start, end + 1);
		addOffset(start, start - end - 1);
		return this;
	}


	public void clear() {
		this.content = new StringBuilder();
		this.content.append(classContent);
		this.imports = getImports(classContent);
		this.offset = new HashMap<>();
	}

	private int getPosition(int position) {
		int output = position;
		List<Integer> offsets = new ArrayList<>(offset.keySet());
		Collections.sort(offsets);
		for (Integer i : offsets) {
			if (i <= output) {
				output += offset.get(i);
			}
		}
		return output;
	}

	public int getPositionNewLine(int position) {
		int originalPosition = getPosition(position) - 1;
		return content.indexOf("\n", originalPosition) - originalPosition;
	}

	private void addOffset(int start, int length) {
		if (length != 0) {
			if (offset.containsKey(start)) {
				offset.put(start, offset.get(start) + length);
			} else {
				offset.put(start, length);
			}
		}
	}

	private void printImports(Set<String> missingImports) {
		int positionImport = content.indexOf("import");
		if (positionImport == -1) {
			positionImport = content.indexOf(";\n") + 1;
		}
		for (String imp : missingImports) {
			this.imports.add(imp.replace("import ", "").replace("static ", ""));
			String content = imp + ";\n";
			this.content.insert(positionImport, content);
			addOffset(positionImport, content.length());
		}
	}

	private Set<String> computeMissingImports(Collection<CtReference> neededImports) {
		Set<String> imports = new HashSet<>();
		for (CtReference neededImport1 : neededImports) {
			String neededImport = printer.getElementPrinterHelper().printImport(neededImport1);

			if (!"".equals(neededImport)
					&& !this.imports.contains(neededImport.replace("import ", "").replace("static ", ""))) {
				imports.add(neededImport);
			}
		}
		return imports;
	}

	private List<String> getImports(String sourceCode) {
		List<String> imports = new ArrayList<>();
		String[] lines = sourceCode.split("\n");
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i].trim();
			if (line.startsWith("import ")) {
				line = line.substring(7, line.length() - 1);
				if (line.startsWith("static")) {
					line = line.substring(6);
				}
				imports.add(line);
			}
		}
		return imports;
	}

	private int getIndentation(int position) {
		int output = 0;
		boolean isSpace = false;
		String[] lines = this.content.substring(position).split("\n");
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.isEmpty()) {
				continue;
			}
			for (int j = 0; j < line.length(); j++) {
				if (line.charAt(j) == ' ' || line.charAt(j) == '\t') {
					isSpace = line.charAt(j) == ' ';
					output++;
				} else {
					break;
				}
			}
			break;
		}
		return isSpace ? output / 4 : output;
	}

	@Override
	public String toString() {
		return content.toString();
	}
}
