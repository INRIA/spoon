package spoon.smpl;

import fr.inria.controlflow.BranchKind;
import fr.inria.controlflow.ControlFlowNode;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Transformer contains methods capable to applying a set of transformations as recorded by
 * CTL-VW witnesses to a CFGModel.
 *
 * A CTL-VW witness is a 4-tuple of the form (state, metavar, binding, subWitnesses) where
 * subWitnesses is a set of witnesses.
 *
 * Transformations are generally encoded as the following witness structure:
 *
 * {(_, x1, y1, {(_, x2, y2, ... {(S, _, Operation)})})}
 *
 * The transformation in the structure above would be applied by collecting all bindings along
 * the path to the leaf witness node and applying its Operation to state S in the CFGModel
 * supplying the Operation with the bindings collected along the path (x1=y1, x2=y2, ...).
 */
public class Transformer {
    // TODO: a method that applies transformations to a method header

    /**
     * Apply a set of transformations to the control flow graph of a method body.
     * @param model CFG of method body
     * @param witnesses CTL-VW witnesses that encode transformations
     */
    public static void transform(CFGModel model, Set<ModelChecker.Witness> witnesses) {
        Map<String, Object> bindings = new HashMap<>();

        for (ModelChecker.Witness witness : witnesses) {
            transform(model, bindings, witness);
        }
    }

    /**
     * Apply a set of transformations using a given set of metavariable bindings to the control
     * flow graph of a method body.
     * @param model CFG of method body
     * @param bindings Metavariable bindings
     * @param witness CTL-VW witness that encodes zero or more transformations
     */
    private static void transform(CFGModel model, Map<String, Object> bindings, ModelChecker.Witness witness) {
        if (witness.binding instanceof List<?>) {
            // The witness binding is a list of operations, apply them

            List<?> objects = (List<?>) witness.binding;

            // Process any PrependOperations in the list
            objects.stream().filter((obj) -> obj instanceof PrependOperation).forEachOrdered((obj) -> {
                ControlFlowNode node = model.getCfg().findNodeById(witness.state);
                BranchKind kind = node.getKind();

                if (kind == BranchKind.STATEMENT) {
                    ((PrependOperation) obj).accept(node.getStatement(), bindings);
                } else if (kind == BranchKind.BRANCH || kind == BranchKind.BLOCK_BEGIN || kind == BranchKind.CONVERGE) {
                    ((PrependOperation) obj).accept(((SmPLMethodCFG.NodeTag) node.getTag()).getAnchor(), bindings);
                } else {
                    throw new IllegalArgumentException("unexpected node kind " + kind);
                }
            });

            // Process any AppendOperations in the list, in reverse order to preserve correct output order
            objects.stream().filter((obj) -> obj instanceof AppendOperation)
                    .collect(Collectors.toCollection(LinkedList::new))
                    .descendingIterator().forEachRemaining((obj) -> {
                        ControlFlowNode node = model.getCfg().findNodeById(witness.state);
                        BranchKind kind = node.getKind();

                        if (kind == BranchKind.STATEMENT) {
                            ((AppendOperation) obj).accept(node.getStatement(), bindings);
                        } else if (kind == BranchKind.BRANCH || kind == BranchKind.BLOCK_BEGIN || kind == BranchKind.CONVERGE) {
                            ((AppendOperation) obj).accept(((SmPLMethodCFG.NodeTag) node.getTag()).getAnchor(), bindings);
                        } else {
                            throw new IllegalArgumentException("unexpected node kind " + kind);
                        }
                    });

            // Process any DeleteOperations in the list
            objects.stream().filter((obj) -> obj instanceof DeleteOperation).forEachOrdered((obj) -> {
                ((DeleteOperation) obj).accept(model.getCfg().findNodeById(witness.state).getStatement(), bindings);
            });

            // Finally process any other Operations
            objects.stream().filter((obj) -> !(obj instanceof PrependOperation))
                            .filter((obj) -> !(obj instanceof AppendOperation))
                            .filter((obj) -> !(obj instanceof DeleteOperation))
                            .filter((obj) -> obj instanceof Operation).forEachOrdered((obj) -> {
                ((Operation) obj).accept(model.getCfg().findNodeById(witness.state).getStatement(), bindings);
            });
        } else if (witness.binding instanceof Operation) {
            // TODO: get rid of this case?
            // The witness binding is a single operation, apply it
            ((Operation) witness.binding).accept(model.getCfg().findNodeById(witness.state).getStatement(), bindings);
        } else {
            // The witness binding is an actual metavariable binding, record it and process sub-witnesses
            bindings.put(witness.metavar, witness.binding);

            for (ModelChecker.Witness subWitness : witness.witnesses) {
                transform(model, bindings, subWitness);
            }

            bindings.remove(witness.metavar);
        }
    }
}
