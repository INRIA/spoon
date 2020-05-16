package spoon.leafactorci.engine;


import spoon.processing.Processor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Refactoring rule interface
 */
public interface RefactoringRule<E extends CtElement> extends Processor<E>, Iteration, CaseDetector, CaseTransformer, CaseProcessor {

    /**
     * Finds a element of interest
     * @param root The baseline element
     * @param isCtElementOfInterest A predicate that identifies the interested element
     * @return True if the element of interest was found in the tree, false otherwise
     */
    static boolean hasCtElementOfInterest(CtElement root, Predicate<CtElement> isCtElementOfInterest) {
        return isCtElementOfInterest.test(root) ||
                root.getDirectChildren().stream()
                        .anyMatch(element -> hasCtElementOfInterest(element, isCtElementOfInterest));
    }

    /**
     * Finds a element of interest
     * @param root The baseline element
     * @param isElementOfInterest A predicate that identifies the interested element
     * @return True if the element of interest was found in the tree, false otherwise
     */
    static <T> List<T> getCtElementsOfInterest(CtElement root, Predicate<CtElement> isElementOfInterest, Class<T> type) {
        Stream<T> a = isElementOfInterest.test(root) ? Stream.of(type.cast(root)) : Stream.empty();
        Stream<T> b = root.getDirectChildren().stream()
                .map(element -> RefactoringRule.getCtElementsOfInterest(element, isElementOfInterest, type))
                .flatMap(List::stream);
        return Stream.concat(a, b).collect(Collectors.toList());
    }

    /**
     * Finds a element of interest with filter
     * @param root The baseline element
     * @param isElementOfInterest A predicate that identifies the interested element
     * @return True if the element of interest was found in the tree, false otherwise
     */
    static <T> List<T> getCtElementsOfInterestWithFilter(CtElement root, Predicate<CtElement> isElementOfInterest, Predicate<CtElement> filter, Class<T> type) {
        if(filter.test(root)) {
            return new ArrayList<T>();
        }
        Stream<T> a = isElementOfInterest.test(root) ? Stream.of(type.cast(root)) : Stream.empty();
        Stream<T> b = root.getDirectChildren().stream()
                .map(element -> RefactoringRule.getCtElementsOfInterestWithFilter(element, isElementOfInterest, filter, type))
                .flatMap(List::stream);
        return Stream.concat(a, b).collect(Collectors.toList());
    }

    /**
     * The closest T by bubbling up
     * @param element The element from which to start
     * @return The closest T by bubbling up
     */
    static <T> T getClosestTypeParent(CtElement element, Class<T> type, List<CtElement> stopAt) {
        CtElement root = element.getParent();
        while (root != null  && !(type.isInstance(root)) && !stopAt.contains(root)) {
            root = root.getParent();
        }
        if(root == null || stopAt.contains(root)) {
            return null;
        }
        return type.cast(root);
    }

    /**
     * The closest CtBlock by bubbling up
     * @param element The element from which to start
     * @return The closest CtBlock by bubbling up
     */
    static CtBlock getClosestBlockParent(CtElement element) {
        CtElement root = element.getParent();
        while (root != null  && !(root instanceof CtBlock)) {
            root = root.getParent();
        }
        if(root == null) {
            return null;
        }
        return (CtBlock)root;
    }

    /**
     * The closest CtClass by bubbling up
     * @param element The element from which to start
     * @return The closest CtClass by bubbling up
     */
    static CtClass getClosestClassParent(CtElement element) {
        CtElement root = element.getParent();
        while (root != null  && !(root instanceof CtClass)) {
            root = root.getParent();
        }
        if(root == null) {
            return null;
        }
        return (CtClass)root;
    }

    /**
     * The closest Method by bubbling up
     * @param element The element from which to start
     * @return The closest MethodDeclaration by bubbling up
     */
    static CtMethod getClosestMethodParent(CtElement element) {
        CtElement root = element.getParent();
        while (root != null  && !(root instanceof CtMethod)) {
            root = root.getParent();
        }
        if(root == null) {
            return null;
        }
        return (CtMethod)root;
    }
}

