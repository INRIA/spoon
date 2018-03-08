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
package spoon.pattern.node;

import java.util.List;

import spoon.pattern.PatternBuilder;

/**
 * The AST model based parameterized model, which can generate or match other AST models.
 *
 * The instance is created by {@link PatternBuilder}
 */
public class ModelNode extends ListOfNodes {

	public ModelNode(List<RootNode> nodes) {
		super(nodes);
	}

/*
	private static class SubstReqOnPosition {
		final int sourceStart;
		final CtElement sourceElement;
		final Node valueResolver;
		SubstReqOnPosition(CtElement sourceElement, Node substReq) {
			this.sourceElement = sourceElement;
			this.sourceStart = getSourceStart(sourceElement);
			this.valueResolver = substReq;
		}

		@Override
		public String toString() {
			return String.valueOf(sourceStart) + ":" + valueResolver;
		}
	}


	@Override
	public String toString() {
		Factory f = getFactory();
		Environment env = f.getEnvironment();
		final List<SubstReqOnPosition> allRequestWithSourcePos = new ArrayList<>();
		for (Map.Entry<CtElement, Node> e : patternElementToSubstRequests.entrySet()) {
			allRequestWithSourcePos.add(new SubstReqOnPosition(e.getKey(), e.getValue()));
		}
		allRequestWithSourcePos.sort((a, b) -> a.sourceStart - b.sourceStart);
		class Iter {
			int off = 0;
			List<SubstReqOnPosition> getAndRemoveRequestUntil(int sourcePos) {
				List<SubstReqOnPosition> res = new ArrayList<>();
				while (off < allRequestWithSourcePos.size() && allRequestWithSourcePos.get(off).sourceStart <= sourcePos) {
					res.add(allRequestWithSourcePos.get(off));
					off++;
				}
				return res;
			}
		}
		Iter iter = new Iter();
//		PrinterHelper printerHelper = new PrinterHelper(env);
//		DefaultTokenWriter tokenWriter = new DefaultTokenWriter(printerHelper);
		DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(env) {
			protected void enter(CtElement e) {
				int sourceStart = getSourceStart(e);
				List<SubstReqOnPosition> requestOnPos = iter.getAndRemoveRequestUntil(sourceStart);
				if (requestOnPos.size() > 0) {
					getPrinterTokenWriter()
						.writeComment(f.createComment(getSubstitutionRequestsDescription(e, sourceStart, requestOnPos), CommentType.BLOCK))
						.writeln();
				}
			}

		};
		try {
			for (CtElement ele : patternModel) {
				printer.computeImports(ele);
			}
			for (CtElement ele : patternModel) {
				printer.scan(ele);
			}
		} catch (ParentNotInitializedException ignore) {
			return "Failed with: " + ignore.toString();
		}
		// in line-preservation mode, newlines are added at the beginning to matches the lines
		// removing them from the toString() representation
		return printer.toString().replaceFirst("^\\s+", "");
	}

	private static int getSourceStart(CtElement ele) {
		while (true) {
			SourcePosition sp = ele.getPosition();
			if (sp != null && sp.getSourceStart() >= 0) {
				//we have found a element with source position
				return sp.getSourceStart();
			}
			if (ele.isParentInitialized() == false) {
				return -1;
			}
			ele = ele.getParent();
		}
	}

*/
}
