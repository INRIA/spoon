package spoon.smpl;

import fr.inria.controlflow.BranchKind;
import fr.inria.controlflow.ControlFlowNode;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;

import java.util.*;

/**
 * A CFGModel builds a CTL model from a given SmPL-adapted CFG.
 */
public class CFGModel implements Model {
    /**
     * Create a new CTL model from a given SmPL-adapted CFG.
     * @param cfg SmPL-adapted CFG to use as model
     */
    public CFGModel(SmPLMethodCFG cfg) {
        this.cfg = cfg;

        states = new ArrayList<>();
        successors = new HashMap<>();
        labels = new HashMap<>();

        for (ControlFlowNode node : cfg.vertexSet()) {
            if (node.getKind() == BranchKind.BEGIN) {
                // Dont add a state for the BEGIN node
                continue;
            }

            int state = node.getId();

            // Add a state ID for each vertex ID and prepare lists of successors and labels
            states.add(state);
            successors.put(state, new ArrayList<Integer>());
            labels.put(state, new ArrayList<Label>());

            // Add successors
            cfg.outgoingEdgesOf(node).forEach(edge -> {
                successors.get(state).add(edge.getTargetNode().getId());
            });

            // Add self-loop on exit node
            if (node.getKind() == BranchKind.EXIT) {
                successors.get(state).add(state);
            }

            CtElement stmt = node.getStatement();

            // Add label
            switch (node.getKind()) {
                case BRANCH:
                    labels.get(state).add(new BranchLabel(stmt));
                    break;
                case STATEMENT:
                    if (SmPLMethodCFG.isMethodHeaderNode(node)) {
                        labels.get(state).add(new PropositionLabel("methodHeader"));

                        SmPLMethodCFG.NodeTag nodeTag = (SmPLMethodCFG.NodeTag) node.getTag();
                        labels.get(state).add(new MethodHeaderLabel((CtMethod<?>) nodeTag.getAnchor()));
                    } else if (SmPLMethodCFG.isUnsupportedElementNode(node)) {
                        labels.get(state).add(new PropositionLabel("unsupported"));
                    } else {
                        labels.get(state).add(new StatementLabel(stmt));
                    }
                    break;
                case BLOCK_BEGIN:
                case TRY:
                case CATCH:
                case FINALLY:
                case CONVERGE:
                    SmPLMethodCFG.NodeTag tag = (SmPLMethodCFG.NodeTag) node.getTag();

                    if (tag != null) {
                        labels.get(state).add(new PropositionLabel(tag.getLabel()));

                        for (String key : tag.getMetadataKeys()) {
                            labels.get(state).add(new MetadataLabel(key, tag.getMetadata(key)));
                        }
                    }
                    break;
                case EXIT:
                    labels.get(state).add(new PropositionLabel("end"));
                    break;
                default:
                    throw new IllegalStateException("unsupported kind " + node.getKind().toString());
            }
        }
    }

    /**
     * @return the set of state IDs in the model
     */
    @Override
    public List<Integer> getStates() {
        return states;
    }

    /**
     * @param state Parent state
     * @return the set of immediate successors of the given state
     */
    @Override
    public List<Integer> getSuccessors(int state) {
        return successors.get(state);
    }

    /**
     * @param state Target state
     * @return the set of labels associated with the given state
     */
    @Override
    public List<Label> getLabels(int state) {
        return labels.get(state);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        List<Integer> states = getStates();
        Collections.sort(states);

        sb.append("CFGModel(states=").append(states).append(", ")
          .append("successors={");

        for (int state : states) {
            for (int next : getSuccessors(state)) {
                sb.append(state).append("->").append(next).append(", ");
            }
        }

        sb.delete(sb.length() - 2, sb.length());
        sb.append("}, labels={");

        for (int state : states) {
            if (getLabels(state).size() > 0 ) {
                sb.append(state).append(": [");

                for (Label label : getLabels(state)) {
                    sb.append(label.toString()).append(", ");
                }

                sb.delete(sb.length() - 2, sb.length());
                sb.append("], ");
            } else {
                sb.append(state).append(": [], ");
            }
        }

        sb.delete(sb.length() - 2, sb.length());
        sb.append("})");

        return sb.toString();
    }

    /**
     * @return the SmPL-adapted CFG used to generate the model
     */
    public SmPLMethodCFG getCfg() { return cfg; }

    /**
     * The SmPL-adapted CFG used to generate the model.
     */
    private SmPLMethodCFG cfg;

    /**
     * The set of state IDs.
     */
    private List<Integer> states;

    /**
     * The set of immediate successors.
     */
    private Map<Integer, List<Integer>> successors;

    /**
     * The set of state labels.
     */
    private Map<Integer, List<Label>> labels;
}
