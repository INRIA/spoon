/**
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.sniper;

import com.cloudbees.diff.Diff;
import spoon.SpoonException;
import spoon.compiler.Environment;
import spoon.reflect.CtModel;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.PrettyPrinter;
import spoon.support.Experimental;
import spoon.support.modelobs.SourceFragmentCreator;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Experimental
public class DiffGenerator {

	private final SourceFragmentCreator fragmentCreator;
	private SniperJavaPrettyPrinter sniper;
	private String diffRoot;
	private boolean ignoreWhiteSpace = false;
	private int numContextLines = 1;

	/**
	 * Creates a new {@link PrettyPrinter} which copies origin sources and prints only diff.
	 */
	public DiffGenerator(Environment env, String diffRoot) {
		this.sniper = new SniperJavaPrettyPrinter(env);
		this.diffRoot = diffRoot;
		if (!diffRoot.isEmpty() && diffRoot.charAt(diffRoot.length() - 1) != '/') {
			this.diffRoot += "/";
		}
		this.fragmentCreator = new SourceFragmentCreator();
		this.fragmentCreator.attachTo(env);
	}

	public void setIgnoreWhiteSpace(boolean ignoreWhiteSpace) {
		this.ignoreWhiteSpace = ignoreWhiteSpace;
	}

	public void setNumContextLines(int numContextLines) {
		this.numContextLines = numContextLines;
	}
	/**
	 * Generates the diff between the original source code and the modified model.
	 */
	public String diff(CtModel model) {
		StringBuilder sb = new StringBuilder();
		Set<CompilationUnit> compilationUnits = new HashSet<>();
		List<CtType> newTypes = new ArrayList<>();
		for (CtType<?> type : model.getAllTypes()) {
			if (type.getPosition().getCompilationUnit() != null) {
				compilationUnits.add(type.getPosition().getCompilationUnit());
			} else {
				newTypes.add(type);
			}
		}

		// print changes
		for (CompilationUnit ctCompilationUnit : compilationUnits) {
			String newContent = "";
			if (!ctCompilationUnit.getDeclaredTypes().isEmpty()) {
				newContent = this.sniper.printCompilationUnit(ctCompilationUnit);
			}
			sb.append(generateDiff(ctCompilationUnit, newContent));
		}
		// print removed type diff
		Collection<CompilationUnit> originalCompilationUnits = model.getRootPackage().getFactory().CompilationUnit().getMap().values();
		originalCompilationUnits.removeAll(compilationUnits);
		for (CompilationUnit ctCompilationUnit : originalCompilationUnits) {
			sb.append(generateDiff(ctCompilationUnit, ""));
		}

		// print new type diff
		for (CtType type : newTypes) {
			CompilationUnit compilationUnit = type.getFactory().CompilationUnit().getOrCreate(type);
			String newContent = type.getFactory().getEnvironment().createPrettyPrinter().printCompilationUnit(compilationUnit);
			sb.append(generateDiff(compilationUnit, newContent));
		}
		return sb.toString();
	}

	private String generateDiff(CtCompilationUnit compilationUnit, String generateContent) {
		String originalSourceCode = "";
		if (compilationUnit.getFile().exists()) {
			originalSourceCode = compilationUnit.getOriginalSourceCode();
		}
		String path = compilationUnit.getFile().getPath();
		path = path.replace(diffRoot, "");

		return generateDiff(path, originalSourceCode, generateContent);
	}

	private String generateDiff(String path, String originalSourceCode, String generateContent) {
		String diff;
		try {
			Diff diffResult = Diff.diff(
					new StringReader(originalSourceCode),
					new StringReader(generateContent),
					this.ignoreWhiteSpace);
			if (diffResult.isEmpty()) {
				return "";
			}
			diff =	diffResult.toUnifiedDiff(path,
					path,
					new StringReader(originalSourceCode),
					new StringReader(generateContent),
					this.numContextLines);
		} catch (IOException e) {
			throw new SpoonException("Unable to generate the diff.", e);
		}

		return diff.replaceAll("\n\\\\ No newline at end of file", "");
	}
}
