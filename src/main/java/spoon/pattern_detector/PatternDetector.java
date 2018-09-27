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
package spoon.pattern_detector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import spoon.pattern.internal.node.ListOfNodes;
import spoon.pattern_detector.internal.gumtree.SpoonGumTreeBuilder;
import spoon.reflect.code.CtComment;
import spoon.reflect.declaration.CtElement;

/**
 * Detects patterns in code
 */
public class PatternDetector {

	private final List<FoundPattern> foundPatterns = new ArrayList<>();
	private final SpoonGumTreeBuilder gumTreeBuilder = new SpoonGumTreeBuilder();
	/**
	 * List of all different patterns, which are needed to represent the processed code
	 */
	private final List<ListOfNodes> patterns = new ArrayList<>();

	/**
	 * check if the code matches with some known pattern. If yes, then done.
	 * If part is matching, then add variable into patterns to make it matching too
	 * If not matching at all then add new pattern
	 * @param code the element where we detect patterns
	 */
	public void matchCode(CtElement code) {
		matchCode(Collections.singletonList(code));
	}

	/**
	 * check if the code matches with some known pattern. If yes, then done.
	 * If part is matching, then add variable into patterns to make it matching too
	 * If not matching at all then add new pattern
	 * @param code the elements where we detect patterns
	 */
	public void matchCode(List<? extends CtElement> code) {
		CodeInfo codeInfo = createCodeInfo(code);
		for (FoundPattern existingPattern : foundPatterns) {
			//found differences between already foundPattern and new code
			if (existingPattern.addMerge(codeInfo)) {
				//existingPattern accepts this new code
				//we are done
				return;
			}
		}
		//no existing pattern can be merged with code
		//add new pattern
		foundPatterns.add(new FoundPattern(codeInfo));
	}

	private CodeInfo createCodeInfo(List<? extends CtElement> code) {
		return new CodeInfo(code, gumTreeBuilder.getTree(null, code));
	}

	public List<FoundPattern> getPatterns() {
		return Collections.unmodifiableList(foundPatterns);
	}

	/**
	 * @param ignoreComments true if comments have to be ignored
	 * @return this to support fluent API
	 */
	public PatternDetector setIgnoreComments(boolean ignoreComments) {
		if (ignoreComments) {
			gumTreeBuilder.setFilter(e -> !(e instanceof CtComment));
		} else {
			gumTreeBuilder.setFilter(null);
		}
		return this;
	}
}
