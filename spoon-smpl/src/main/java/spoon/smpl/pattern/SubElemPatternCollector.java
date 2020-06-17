package spoon.smpl.pattern;

import java.util.ArrayList;
import java.util.List;

/**
 * A SubElemPatternCollector collects the list of all ElemNodes sub-patterns contained in a given Pattern.
 */
public class SubElemPatternCollector implements PatternNodeVisitor {
	/**
	 * Create a new SubElemPatternCollctor.
	 */
	public SubElemPatternCollector() {
		subPatterns = new ArrayList<>();
	}

	/**
	 * Get the list of ElemNode sub-patterns.
	 *
	 * @return List of ElemNode sub-patterns
	 */
	public List<ElemNode> getResult() {
		return subPatterns;
	}

	@Override
	public void visit(ElemNode node) {
		subPatterns.add(node);

		for (String key : node.sub.keySet()) {
			node.sub.get(key).accept(this);
		}
	}

	@Override
	public void visit(ParamNode node) {
	}

	@Override
	public void visit(ValueNode node) {
	}

	/**
	 * Storage for resulting list of ElemNode sub-patterns.
	 */
	private List<ElemNode> subPatterns;
}
