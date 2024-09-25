/*
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package spoon.smpl;

import fr.inria.controlflow.BranchKind;
import fr.inria.controlflow.ControlFlowBuilder;
import fr.inria.controlflow.ControlFlowEdge;
import fr.inria.controlflow.ControlFlowGraph;
import fr.inria.controlflow.ControlFlowNode;
import fr.inria.controlflow.ExceptionControlFlowStrategy;
import fr.inria.controlflow.NaiveExceptionControlFlowStrategy;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBreak;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtSuperAccess;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.CtScanner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static fr.inria.controlflow.NaiveExceptionControlFlowStrategy.Options.AddPathsForEmptyTryBlocks;
import static fr.inria.controlflow.NaiveExceptionControlFlowStrategy.Options.ReturnWithoutFinalizers;

import org.slf4j.LoggerFactory;

// TODO: rename since its not just methods but rather executables?

/**
 * An SmPLMethodCFG creates and adapts a ControlFlowGraph for a given executable such as to make it
 * suitable for use in an SmPL context.
 */
public class SmPLMethodCFG {
	/**
	 * UnsupportedElementSwapper takes a CtExecutable and replaces unsupported AST elements e with elements e'
	 * such that UnsupportedElementSwapper.isUnsupportedElementMarker(e') will hold true. The replacements
	 * can be reversed by calling the method restore().
	 */
	public static class UnsupportedElementSwapper extends CtScanner {
		/**
		 * Replace all unsupported elements of a given executable with "unsupported element markers".
		 *
		 * @param ctExecutable Executable to process
		 */
		public UnsupportedElementSwapper(CtExecutable<?> ctExecutable) {
			this.ctExecutable = ctExecutable;

			isRestoring = false;
			swappedElements = new HashMap<>();
			swapIndex = 0;

			ctExecutable.accept(this);
		}

		/**
		 * Check whether a given element is a marker substituting an unsupported element.
		 *
		 * @param e Element to check
		 * @return True if the element is a marker substituting an unsupported element
		 */
		public static boolean isUnsupportedElementMarker(CtElement e) {
			return (e instanceof CtInvocation
					&& ((CtInvocation<?>) e).getTarget() == null
					&& ((CtInvocation<?>) e).getExecutable().getSimpleName().equals(SmPLJavaDSL.getUnsupportedElementName()));
		}

		/**
		 * Restore all replacements.
		 */
		public void restore() {
			isRestoring = true;
			ctExecutable.accept(this);
			isRestoring = false;
		}

		@Override
		public void visitCtWhile(CtWhile whileLoop) {
			replace(whileLoop);
		}

		@Override
		public void visitCtFor(CtFor forLoop) {
			replace(forLoop);
		}

		@Override
		public void visitCtForEach(CtForEach forLoop) {
			replace(forLoop);
		}

		@Override
		public void visitCtBreak(CtBreak breakStatement) {
			replace(breakStatement);
		}

		@Override
		public <S> void visitCtSwitch(CtSwitch<S> switchStatement) {
			replace(switchStatement);
		}

		@Override
		public <T> void visitCtNewArray(CtNewArray<T> newArray) {
			replace(newArray);
		}

		@Override
		public <T> void visitCtNewClass(CtNewClass<T> newClass) {
			replace(newClass);
		}

		@Override
		public <T> void visitCtSuperAccess(CtSuperAccess<T> superAccess) {
			replace(superAccess);
		}

		@Override
		public <T> void visitCtInvocation(CtInvocation<T> invocation) {
			if (!isRestoring || !isUnsupportedElementMarker(invocation)) {
				super.visitCtInvocation(invocation);
			} else {
				int index = ((CtLiteral<Integer>) invocation.getArguments().get(0)).getValue();

				invocation.replace(swappedElements.get(index));
			}
		}

		/**
		 * Replace a single element.
		 *
		 * @param e Element to replace
		 */
		private void replace(CtElement e) {
			if (isRestoring) {
				throw new IllegalStateException("there should never be a call to replace() while restoring");
			}

			logReplacementWarning(e);

			swappedElements.put(swapIndex, e);
			e.replace(createReplacementInvocation(e.getFactory(), swapIndex));

			swapIndex += 1;
		}

		/**
		 * Log a warning about a replaced element.
		 * @param e Element about to be replaced
		 */
		private void logReplacementWarning(CtElement e) {
			if (!e.getPosition().isValidPosition()) {
				String code = e.toString();
				LoggerFactory.getLogger("spoon-smpl")
					.warn("Unsupported element excluded from control flow graph at unknown location: "
						+ e.getClass().getSimpleName()
						+ code.substring(0, Math.min(40, code.length())));
				return;
			}
			String file = Optional.ofNullable(e.getPosition())
									.map(x -> x.getFile())
									.map(x -> x.getName())
									.orElse("[unknown file]");

			String line = Optional.ofNullable(e.getPosition())
									.map(x -> x.getLine())
									.map(x -> Integer.toString(x))
									.orElse("-");

			String code = Optional.ofNullable(e.getPosition())
									.map(x -> x.getFile())
									.map(x -> e.getOriginalSourceFragment())
									.map(x -> x.getSourceCode())
									.orElse(e.toString());

			code = code.strip()
				.replace("\n", " ")
				.substring(0, Math.min(40, code.length()));

			LoggerFactory.getLogger("spoon-smpl")
					.warn("Unsupported element excluded from control flow graph: "
						+ e.getClass().getSimpleName()
						+ "(" + file + ":" + line + " \"" +  code + " ..\")");
		}

		/**
		 * Create a marker element for substituting a single unsupported element.
		 *
		 * @param factory Spoon Factory
		 * @param index   Replacement index
		 * @return Marker element
		 */
		private static CtInvocation<Void> createReplacementInvocation(Factory factory, int index) {
			CtExecutableReference<Void> exe = factory.createExecutableReference();
			exe.setType(factory.createCtTypeReference(Void.class));
			exe.setSimpleName(SmPLJavaDSL.getUnsupportedElementName());
			exe.setParameters(Arrays.asList(factory.createCtTypeReference(int.class)));

			List<CtExpression<?>> args = Arrays.asList(factory.createLiteral(index));

			return factory.createInvocation(null, exe, args);
		}

		/**
		 * Target method.
		 */
		private CtExecutable<?> ctExecutable;

		/**
		 * Instance state flag signalling whether or not we are currently in "restore" mode, which if true
		 * alters the behavior when scanning replacement marker elements.
		 */
		private boolean isRestoring;

		/**
		 * Replacement index counter.
		 */
		private int swapIndex;

		/**
		 * Replacement storage.
		 */
		private Map<Integer, CtElement> swappedElements;
	}

	/**
	 * A NodeTag is a combination of a String label and an AST element anchor.
	 * <p>
	 * The idea is that certain nodes (BRANCH, BLOCK_BEGIN and CONVERGE) in an SmPL-adapted CFG should
	 * be tagged with a NodeTag using labels "branch", ("trueBranch" or "falseBranch") and "after",
	 * respectively, along with the appropriate AST element for anchoring prepend and/or append operations
	 * to.
	 */
	public static class NodeTag {
		/**
		 * Create a new NodeTag.
		 *
		 * @param label  Label
		 * @param anchor Anchor element
		 */
		public NodeTag(String label, CtElement anchor) {
			this.label = label;
			this.anchor = anchor;
			this.metadata = new HashMap<>();
		}

		/**
		 * Get the label string.
		 *
		 * @return Label string
		 */
		public String getLabel() {
			return label;
		}

		/**
		 * Get the anchor element.
		 *
		 * @return Anchor element
		 */
		public CtElement getAnchor() {
			return anchor;
		}

		public List<String> getMetadataKeys() {
			List<String> result = new ArrayList<>(metadata.keySet());
			Collections.sort(result);
			return result;
		}

		public void setMetadata(String key, Object value) {
			metadata.put(key, value);
		}

		public Object getMetadata(String key) {
			return metadata.getOrDefault(key, null);
		}

		@Override
		public String toString() {
			return "NodeTag(" + label + ", " + anchor.toString() + ", " + metadata.toString() + ")";
		}

		/**
		 * Label string.
		 */
		private final String label;

		/**
		 * Anchor element.
		 */
		private final CtElement anchor;

		/**
		 * Arbitrary metadata key-value store.
		 */
		private final Map<String, Object> metadata;
	}

	/**
	 * Create a new SmPL-adapted CFG from a given executable.
	 *
	 * @param executable Executable for which to generate an SmPL-adapted CFG
	 */
	public SmPLMethodCFG(CtExecutable<?> executable) {
		this.swapper = new UnsupportedElementSwapper(executable);

		ControlFlowBuilder builder = new ControlFlowBuilder();
		ExceptionControlFlowStrategy strategy = new NaiveExceptionControlFlowStrategy(EnumSet.of(AddPathsForEmptyTryBlocks,
																									ReturnWithoutFinalizers));
		builder.setExceptionControlFlowStrategy(strategy);

		this.cfg = builder.build(executable.getBody());

		int parentId = 0;

		removeBlockEndNodes(cfg);
		removeOutermostBlockBeginNode(cfg);
		removeExceptionBlockBeginNodes(cfg);

		// TODO: document the motivation for replacing ternary branches with single statements
		replaceTernaryBranchesWithSingleStatements(cfg);

		// Add method header node
		cfg.findNodesOfKind(BranchKind.BEGIN).forEach((cfgEntryNode) -> {
			if (cfgEntryNode.next().size() != 1) {
				throw new IllegalArgumentException("invalid BEGIN node in CFG");
			}

			ControlFlowNode cfgEntryNodeSuccessor = cfgEntryNode.next().get(0);

			// Remove the path from the CFG entry node to its successor
			cfg.removeEdge(cfgEntryNode, cfgEntryNodeSuccessor);

			// Create a node for the method header / signature
			ControlFlowNode methodHeaderNode = new ControlFlowNode(executable, cfg, BranchKind.STATEMENT);
			methodHeaderNode.setTag(new NodeTag("methodHeader", executable));

			// Connect CFG entry node -> method header node -> CFG entry successor
			cfg.addEdge(cfgEntryNode, methodHeaderNode);
			cfg.addEdge(methodHeaderNode, cfgEntryNodeSuccessor);
		});

		// Annotate markers for unsupported elements
		cfg.findNodesOfKind(BranchKind.STATEMENT).forEach((node) -> {
			if (UnsupportedElementSwapper.isUnsupportedElementMarker(node.getStatement())) {
				node.setTag(new NodeTag("unsupported", node.getStatement()));
			}
		});

		// Annotate branches
		for (ControlFlowNode node : cfg.findNodesOfKind(BranchKind.BRANCH)) {
			// CtIf is the only supported branch statement at this time
			CtIf ifStm = (CtIf) node.getStatement().getParent();

			int currentParentId = parentId++;

			NodeTag branchTag = new NodeTag("branch", ifStm);
			branchTag.setMetadata("parentId", currentParentId);
			node.setTag(branchTag);

			ControlFlowNode afterNode = findPostBranchConvergenceNode(node);

			if (afterNode != null) {
				NodeTag afterTag = new NodeTag("after", ifStm);
				afterTag.setMetadata("parent", currentParentId);
				afterNode.setTag(afterTag);
			}

			List<ControlFlowNode> branchNodes = new ArrayList<>();

			for (ControlFlowNode innerNode : node.next()) {
				if (innerNode.getKind() != BranchKind.CATCH) {
					branchNodes.add(innerNode);
				}
			}

			if (branchNodes.size() != 2) {
				throw new IllegalStateException("branch node with invalid successors");
			}

			ControlFlowNode n1 = branchNodes.get(0);
			ControlFlowNode n2 = branchNodes.get(1);

			NodeTag trueTag = new NodeTag("trueBranch", ifStm.getThenStatement());
			trueTag.setMetadata("parent", currentParentId);

			NodeTag falseTag = new NodeTag("falseBranch", ifStm.getElseStatement());
			falseTag.setMetadata("parent", currentParentId);

			// If only one successor is a BLOCK_BEGIN the branch is else-less.
			if (n1.getKind() == BranchKind.BLOCK_BEGIN && n2.getKind() == BranchKind.CONVERGE) {
				n1.setTag(trueTag);
			} else if (n2.getKind() == BranchKind.BLOCK_BEGIN && n1.getKind() == BranchKind.CONVERGE) {
				n2.setTag(trueTag);
			} else {
				// Both successors are blocks

				ControlFlowNode n1next = n1.next().get(0);
				ControlFlowNode n2next = n2.next().get(0);

				if (n1next.getKind() == BranchKind.CONVERGE && n2next.getKind() == BranchKind.CONVERGE) {
					// Both blocks are empty, we can choose labels arbitrarily
					n1.setTag(trueTag);
					n2.setTag(falseTag);
				} else {
					// One or both blocks contains statements, must assign correct label

					boolean n1HasStatements = n1next.getStatement() != null;
					boolean n1IsTrueBranch;

					if (n1HasStatements) {
						n1IsTrueBranch = n1next.getStatement().getParent() == ifStm.getThenStatement();
					} else {
						n1IsTrueBranch = n2next.getStatement().getParent() == ifStm.getElseStatement();
					}

					if (n1IsTrueBranch) {
						n1.setTag(trueTag);
						n2.setTag(falseTag);
					} else {
						n1.setTag(falseTag);
						n2.setTag(trueTag);
					}
				}
			}
		}

		// Annotate exception control flow
		for (ControlFlowNode node : cfg.findNodesOfKind(BranchKind.TRY)) {
			CtTry tryStmt = (CtTry) node.getStatement();
			int currentParentId = parentId++;

			NodeTag tryTag = new NodeTag("tryBlock", tryStmt);
			tryTag.setMetadata("parentId", currentParentId);
			node.setTag(tryTag);

			NodeTag catchTag = new NodeTag("catchBlock", tryStmt);
			catchTag.setMetadata("parentId", currentParentId);

			NodeTag finallyTag = new NodeTag("finallyBlock", tryStmt);
			finallyTag.setMetadata("parentId", currentParentId);

			NodeTag convergeTag = new NodeTag("after", tryStmt);
			convergeTag.setMetadata("parentId", currentParentId);

			int nextNodeId = node.getId() + 1;
			ControlFlowNode nextNode = cfg.findNodeById(nextNodeId);

			Set<BranchKind> wantedKinds = new HashSet<>(Arrays.asList(BranchKind.CATCH, BranchKind.FINALLY, BranchKind.CONVERGE));

			while (nextNode != null && wantedKinds.contains(nextNode.getKind())) {
				switch (nextNode.getKind()) {
					case CATCH:
						nextNode.setTag(catchTag);
						break;

					case FINALLY:
						nextNode.setTag(finallyTag);
						break;

					case CONVERGE:
						nextNode.setTag(convergeTag);
						break;

					default:
						throw new IllegalStateException("unreachable");
				}

				nextNodeId += 1;
				nextNode = cfg.findNodeById(nextNodeId);
			}
		}
	}

	public void restoreUnsupportedElements() {
		swapper.restore();
	}

	/**
	 * Check a given node for being the special method header node.
	 *
	 * @param node Node to check
	 * @return True if given node is the special method header node, false otherwise
	 */
	public static boolean isMethodHeaderNode(ControlFlowNode node) {
		return node.getTag() instanceof NodeTag
				&& ((NodeTag) node.getTag()).getLabel().equals("methodHeader");
	}

	/**
	 * Check a given node for being a marker substituting an unsupported element.
	 *
	 * @param node Node to check
	 * @return True if given node is a marker substituting an unsupported element, false otherwise
	 */
	public static boolean isUnsupportedElementNode(ControlFlowNode node) {
		return node.getTag() instanceof NodeTag
				&& ((NodeTag) node.getTag()).getLabel().equals("unsupported");
	}

	/**
	 * @see ControlFlowGraph::vertexSet
	 */
	public Set<ControlFlowNode> vertexSet() {
		return cfg.vertexSet();
	}

	/**
	 * @see ControlFlowGraph::incomingEdgesOf
	 */
	public Set<ControlFlowEdge> incomingEdgesOf(ControlFlowNode vertex) {
		return cfg.incomingEdgesOf(vertex);
	}

	/**
	 * @see ControlFlowGraph::outgoingEdgesOf
	 */
	public Set<ControlFlowEdge> outgoingEdgesOf(ControlFlowNode vertex) {
		return cfg.outgoingEdgesOf(vertex);
	}

	/**
	 * @see ControlFlowGraph::findNodeById
	 */
	public ControlFlowNode findNodeById(int id) {
		return cfg.findNodeById(id);
	}

	/**
	 * @see ControlFlowGraph::findNodesOfKind
	 */
	public List<ControlFlowNode> findNodesOfKind(BranchKind kind) {
		return cfg.findNodesOfKind(kind);
	}

	/**
	 * @see ControlFlowGraph::getExitNode
	 */
	public ControlFlowNode getExitNode() {
		return cfg.getExitNode();
	}

	@Override
	public String toString() {
		return cfg.toGraphVisText();
	}

	/**
	 * Find the post-branch convergence node corresponding to a BRANCH node.
	 *
	 * @param node Branch node to find convergence node for
	 * @return Convergence node corresponding to given branch node
	 */
	private static ControlFlowNode findPostBranchConvergenceNode(ControlFlowNode node) {
		// FIXME: this relies on implementation details of ControlFlowBuilder::visitCtIf and ControlFlowNode.count
		// A good fix would be adding this info explicitly to spoon-control-flow, e.g by having
		// ControlFlowNodes have a field .postBranchConverge that was set to the specific
		// convergence node created for the branch, and having a method ControlFlowNode::getPostBranchConvergenceNode
		return node.getParent().findNodeById(node.getId() + 1);
	}

	/**
	 * Find the post-trycatch convergence node corresponding to a TRY node.
	 *
	 * @param node TRY node to find convergence node for
	 * @return Convergence node corresponding to given TRY node
	 */
	private static ControlFlowNode findPostTryConvergenceNode(ControlFlowNode node) {
		// FIXME: same issue as findPostBranchConvergenceNode
		return node.getParent().findNodeById(node.getId() + 1);
	}

	/**
	 * Remove a node from a CFG, adding edges to preserve paths.
	 *
	 * @param cfg  CFG to operate on
	 * @param node Node to remove
	 */
	private static void removeNode(ControlFlowGraph cfg, ControlFlowNode node) {
		Set<ControlFlowEdge> incoming = cfg.incomingEdgesOf(node);
		Set<ControlFlowEdge> outgoing = cfg.outgoingEdgesOf(node);

		for (ControlFlowEdge in : incoming) {
			for (ControlFlowEdge out : outgoing) {
				cfg.addEdge(in.getSourceNode(), out.getTargetNode());
			}
		}

		cfg.edgesOf(node).forEach(cfg::removeEdge);
		cfg.removeVertex(node);
	}

	/**
	 * Remove all nodes of kind BLOCK_END from a given CFG.
	 *
	 * @param cfg CFG to operate on
	 */
	private static void removeBlockEndNodes(ControlFlowGraph cfg) {
		cfg.findNodesOfKind(BranchKind.BLOCK_END).forEach((node) -> removeNode(cfg, node));
	}

	/**
	 * If the BEGIN node has a BLOCK_BEGIN as successor, remove the BLOCK_BEGIN. This BLOCK_BEGIN
	 * generally represents the start of the block enclosing the full method body.
	 *
	 * @param cfg CFG to operate on
	 */
	private static void removeOutermostBlockBeginNode(ControlFlowGraph cfg) {
		cfg.findNodesOfKind(BranchKind.BEGIN).forEach((node) -> {
			if (node.next().get(0).getKind() == BranchKind.BLOCK_BEGIN) {
				removeNode(cfg, node.next().get(0));
			}
		});
	}

	/**
	 * Remove any BLOCK_BEGIN nodes that are successors to TRY or CATCH nodes.
	 *
	 * @param cfg CFG to operate on
	 */
	private static void removeExceptionBlockBeginNodes(ControlFlowGraph cfg) {
		Set<BranchKind> wantedKinds = new HashSet<>(Arrays.asList(BranchKind.TRY, BranchKind.CATCH));

		cfg.findNodesOfKind(BranchKind.BLOCK_BEGIN).forEach(node -> {
			if (node.prev().size() == 1 && wantedKinds.contains(node.prev().get(0).getKind())) {
				removeNode(cfg, node);
			}
		});
	}

	/**
	 * Replace the branches generated by spoon-control-flow for ternary expressions with single statement nodes
	 * containing the full ternary expression statement.
	 *
	 * @param cfg CFG to operate on
	 */
	private static void replaceTernaryBranchesWithSingleStatements(ControlFlowGraph cfg) {
		cfg.findNodesOfKind(BranchKind.BRANCH).forEach(node -> {
			CtElement stmt = node.getStatement();

			// spoon-control-flow 0.0.2 only adds branches for ternary conditionals used in variable declarations and assignment statements
			if ((stmt instanceof CtVariable && ((CtVariable<?>) stmt).getDefaultExpression() instanceof CtConditional)
				|| (stmt instanceof CtAssignment && ((CtAssignment<?, ?>) stmt).getAssignment() instanceof CtConditional)) {

				List<ControlFlowNode> nodesToRemove = new ArrayList<>();

				for (ControlFlowNode innerNode : node.next()) {
					if (innerNode.getKind() != BranchKind.CATCH) {
						nodesToRemove.add(innerNode);
					}
				}

				// FIXME: relies on implementation details of ControlFlowBuilder and ControlFlowNode
				ControlFlowNode postConvergeNode = node.getParent().findNodeById(node.getId() + 1).next().get(0);
				cfg.addEdge(node, postConvergeNode);

				for (ControlFlowNode nodeToRemove : nodesToRemove) {
					removePathWhileUnreachable(nodeToRemove);
				}

				node.setKind(BranchKind.STATEMENT);
			}
		});
	}

	/**
	 * Given a starting node, remove it and iteratively remove any successors that were made unreachable by a
	 * removal until such removals did not cause any new nodes to become unreachable.
	 *
	 * @param start Starting node
	 */
	private static void removePathWhileUnreachable(ControlFlowNode start) {
		Deque<ControlFlowNode> nodesToRemove = new LinkedList<>(Collections.singletonList(start));

		while (!nodesToRemove.isEmpty()) {
			ControlFlowNode node = nodesToRemove.removeFirst();
			node.next().stream().filter(x -> x.prev().size() == 1).forEach(nodesToRemove::addLast);
			node.getParent().removeVertex(node);
		}
	}

	/**
	 * The adapted CFG.
	 */
	private final ControlFlowGraph cfg;

	/**
	 * Swapper for unsupported elements.
	 */
	private UnsupportedElementSwapper swapper;
}
