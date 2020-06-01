package spoon.smpl;

import fr.inria.controlflow.BranchKind;
import fr.inria.controlflow.ControlFlowNode;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;

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
        HashSet<Integer> done = new HashSet<>();

        for (ModelChecker.Witness witness : witnesses) {
            transform(model, bindings, witness, done);
        }
    }

    /**
     * Copy method additions specified by a matching rule to the parent class of a matching method. Only missing
     * methods are copied.
     *
     * @param model CFG Model of matching method
     * @param rule Matching rule
     */
    public static void copyAddedMethods(CFGModel model, SmPLRule rule) {
        copyAddedMethods(model.getCfg().findNodesOfKind(BranchKind.STATEMENT).get(0).getStatement().getParent(CtClass.class), rule);
    }

    /**
     * Copy method additions specified by a matching rule to the parent class of a matching method. Only missing
     * methods are copied.
     *
     * @param cls Parent class of matching method
     * @param rule Matching rule
     */
    public static void copyAddedMethods(CtClass<?> cls, SmPLRule rule) {
        List<String> sigs = new ArrayList<>();

        for (CtMethod<?> method : cls.getMethods()) {
            sigs.add(method.getSignature());
        }

        for (CtMethod<?> method : rule.getMethodsAdded()) {
            if (!sigs.contains(method.getSignature())) {
                cls.addMethod(method);
            }
        }
    }

    /**
     * Apply a set of transformations using a given set of metavariable bindings to the control
     * flow graph of a method body.
     * @param model CFG of method body
     * @param bindings Metavariable bindings
     * @param witness CTL-VW witness that encodes zero or more transformations
     */
    private static void transform(CFGModel model, Map<String, Object> bindings, ModelChecker.Witness witness, Set<Integer> done) {
        if (witness.binding instanceof List<?>) {
            if (done.contains(witness.state)) {
                return;
            }

            done.add(witness.state);

            // The witness binding is a list of operations, apply them

            List<?> objects = (List<?>) witness.binding;
            ControlFlowNode node = model.getCfg().findNodeById(witness.state);
            BranchKind kind = node.getKind();

            CtElement targetElement;

            if (kind == BranchKind.STATEMENT) {
                targetElement = node.getStatement();
            } else if (kind == BranchKind.BRANCH || kind == BranchKind.BLOCK_BEGIN) {
                targetElement = ((SmPLMethodCFG.NodeTag) node.getTag()).getAnchor();
            } else {
                throw new IllegalArgumentException("unexpected node kind " + kind);
            }

            // Process any prepend operations in the list
            objects.stream().filter((obj) -> obj instanceof Operation).forEachOrdered((obj) -> {
                ((Operation) obj).accept(OperationFilter.PREPEND, targetElement, bindings);
            });

            // Process any append operations in the list, in reverse order to preserve correct output order
            objects.stream().filter((obj) -> obj instanceof Operation)
                    .collect(Collectors.toCollection(LinkedList::new))
                    .descendingIterator().forEachRemaining((obj) -> {
                        ((Operation) obj).accept(OperationFilter.APPEND, targetElement, bindings);
                    });

            // Process any delete operations in the list
            objects.stream().filter((obj) -> obj instanceof Operation).forEachOrdered((obj) -> {
                ((Operation) obj).accept(OperationFilter.DELETE, targetElement, bindings);
            });
        } else {
            // The witness binding is an actual metavariable binding, record it and process sub-witnesses
            bindings.put(witness.metavar, witness.binding);

            for (ModelChecker.Witness subWitness : witness.witnesses) {
                transform(model, bindings, subWitness, done);
            }

            bindings.remove(witness.metavar);
        }
    }
}
