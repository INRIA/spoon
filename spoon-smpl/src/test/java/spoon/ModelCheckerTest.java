package spoon;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import spoon.Launcher;
//import spoon.pattern.Pattern;
//import spoon.pattern.PatternBuilder;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.smpl.*;
import spoon.smpl.formula.*;
import spoon.smpl.pattern.*;

import java.util.*;

public class ModelCheckerTest {
    public class ModelBuilder implements Model {
        public List<Integer> states;
        public Map<Integer, List<Integer>> successors;
        public Map<Integer, List<Label>> labels;

        public ModelBuilder() {
            states = new ArrayList<Integer>();
            successors = new HashMap<Integer, List<Integer>>();
            labels = new HashMap<Integer, List<Label>>();
        }

        public ModelBuilder addStates(int ... n) {
            for (int nn : n) {
                states.add(nn);
                successors.put(nn, new ArrayList<Integer>());
                labels.put(nn, new ArrayList<Label>());
            }

            return this;
        }

        public ModelBuilder addTransition(int from, int to) {
            successors.get(from).add(to);
            return this;
        }

        public ModelBuilder addLabel(int state, Label label) {
            labels.get(state).add(label);
            return this;
        }

        @Override
        public List<Integer> getStates() {
            return states;
        }

        @Override
        public List<Integer> getSuccessors(int state) {
            return successors.get(state);
        }

        @Override
        public List<Label> getLabels(int state) {
            return labels.get(state);
        }
    }

    public static Set<Integer> makeSet(Integer ... xs) {
        return new HashSet<Integer>(Arrays.asList(xs));
    }

    @Test
    public void testTrue() {
        ModelBuilder model = new ModelBuilder();
        model.addStates(1,2,3)
                .addTransition(1,2)
                .addTransition(2,3)
                .addTransition(3,3);

        assertTrue(ModelChecker.isValid(model));

        ModelChecker checker = new ModelChecker(model);

        new True().accept(checker);
        assertEquals(makeSet(1,2,3), checker.getResult());
    }

    @Test
    public void testAnd() {
        ModelBuilder model = new ModelBuilder();
        model.addStates(1,2,3)
                .addTransition(1,2)
                .addTransition(2,3)
                .addTransition(3,3)
                .addLabel(1, new PropositionLabel("p"))
                .addLabel(2, new PropositionLabel("q"))
                .addLabel(3, new PropositionLabel("p"))
                .addLabel(3, new PropositionLabel("q"));

        assertTrue(ModelChecker.isValid(model));

        ModelChecker checker = new ModelChecker(model);

        new And(new Proposition("p"), new Proposition("q")).accept(checker);
        assertEquals(makeSet(3), checker.getResult());
    }

    @Test
    public void testOr() {
        ModelBuilder model = new ModelBuilder();
        model.addStates(1,2,3)
                .addTransition(1,2)
                .addTransition(2,3)
                .addTransition(3,3)
                .addLabel(1, new PropositionLabel("p"))
                .addLabel(2, new PropositionLabel("q"))
                .addLabel(3, new PropositionLabel("p"))
                .addLabel(3, new PropositionLabel("q"));

        assertTrue(ModelChecker.isValid(model));

        ModelChecker checker = new ModelChecker(model);

        new Or(new Proposition("p"), new Proposition("q")).accept(checker);
        assertEquals(makeSet(1,2,3), checker.getResult());
    }

    @Test
    public void testNeg() {
        ModelBuilder model = new ModelBuilder();
        model.addStates(1,2,3)
                .addTransition(1,2)
                .addTransition(2,3)
                .addTransition(3,3)
                .addLabel(1, new PropositionLabel("p"))
                .addLabel(2, new PropositionLabel("q"))
                .addLabel(3, new PropositionLabel("p"))
                .addLabel(3, new PropositionLabel("q"));

        assertTrue(ModelChecker.isValid(model));

        ModelChecker checker = new ModelChecker(model);

        new Neg(new Proposition("p")).accept(checker);
        assertEquals(makeSet(2), checker.getResult());

        new Neg(new Proposition("q")).accept(checker);
        assertEquals(makeSet(1), checker.getResult());
    }

    @Test
    public void testProposition() {
        ModelBuilder model = new ModelBuilder();
        model.addStates(1,2,3)
             .addTransition(1,2)
             .addTransition(2,3)
             .addTransition(3,3)
             .addLabel(1, new PropositionLabel("p"))
             .addLabel(2, new PropositionLabel("q"))
             .addLabel(3, new PropositionLabel("p"))
             .addLabel(3, new PropositionLabel("q"));

        assertTrue(ModelChecker.isValid(model));

        ModelChecker checker = new ModelChecker(model);

        new Proposition("p").accept(checker);
        assertEquals(makeSet(1,3), checker.getResult());

        new Proposition("q").accept(checker);
        assertEquals(makeSet(2,3), checker.getResult());

        new Proposition("r").accept(checker);
        assertEquals(makeSet(), checker.getResult());
    }

    @Test
    public void testExistsNext() {
        ModelBuilder model = new ModelBuilder();
        model.addStates(1,2,3,4,5)
                .addTransition(1,2)
                .addTransition(1,3)
                .addTransition(2,2)
                .addTransition(3,4)
                .addTransition(3,5)
                .addTransition(4,3)
                .addTransition(5,3)
                .addLabel(2, new PropositionLabel("p"))
                .addLabel(3, new PropositionLabel("q"))
                .addLabel(4, new PropositionLabel("r"))
                .addLabel(5, new PropositionLabel("r"));

        assertTrue(ModelChecker.isValid(model));

        ModelChecker checker = new ModelChecker(model);

        new ExistsNext(new Proposition("p")).accept(checker);
        assertEquals(makeSet(1,2), checker.getResult());

        new ExistsNext(new Proposition("q")).accept(checker);
        assertEquals(makeSet(1,4,5), checker.getResult());

        new ExistsNext(new Proposition("r")).accept(checker);
        assertEquals(makeSet(3), checker.getResult());
    }

    @Test
    public void testAllNext() {
        ModelBuilder model = new ModelBuilder();
        model.addStates(1,2,3,4,5)
                .addTransition(1,2)
                .addTransition(1,3)
                .addTransition(2,2)
                .addTransition(3,4)
                .addTransition(3,5)
                .addTransition(4,3)
                .addTransition(5,3)
                .addLabel(2, new PropositionLabel("p"))
                .addLabel(3, new PropositionLabel("q"))
                .addLabel(4, new PropositionLabel("r"))
                .addLabel(5, new PropositionLabel("r"));

        assertTrue(ModelChecker.isValid(model));

        ModelChecker checker = new ModelChecker(model);

        new AllNext(new Proposition("p")).accept(checker);
        assertEquals(makeSet(2), checker.getResult());

        new AllNext(new Proposition("q")).accept(checker);
        assertEquals(makeSet(4,5), checker.getResult());

        new AllNext(new Proposition("r")).accept(checker);
        assertEquals(makeSet(3), checker.getResult());
    }

    @Test
    public void testExistsUntil() {
        ModelBuilder model = new ModelBuilder();
        model.addStates(1,2,3,4,5)
                .addTransition(1,2)
                .addTransition(1,5)
                .addTransition(2,3)
                .addTransition(3,4)
                .addTransition(4,4)
                .addTransition(5,4)
                .addLabel(1, new PropositionLabel("p"))
                .addLabel(2, new PropositionLabel("p"))
                .addLabel(3, new PropositionLabel("p"))
                .addLabel(4, new PropositionLabel("q"))
                .addLabel(5, new PropositionLabel("r"));

        assertTrue(ModelChecker.isValid(model));

        ModelChecker checker = new ModelChecker(model);

        new ExistsUntil(new Proposition("p"), new Proposition("q")).accept(checker);
        assertEquals(makeSet(1,2,3,4), checker.getResult());
    }

    @Test
    public void testAllUntil() {
        ModelBuilder model = new ModelBuilder();
        model.addStates(1,2,3,4,5)
                .addTransition(1,2)
                .addTransition(1,5)
                .addTransition(2,3)
                .addTransition(3,4)
                .addTransition(4,4)
                .addTransition(5,4)
                .addLabel(1, new PropositionLabel("p"))
                .addLabel(2, new PropositionLabel("p"))
                .addLabel(3, new PropositionLabel("p"))
                .addLabel(4, new PropositionLabel("q"))
                .addLabel(5, new PropositionLabel("r"));

        assertTrue(ModelChecker.isValid(model));

        ModelChecker checker = new ModelChecker(model);

        new AllUntil(new Proposition("p"), new Proposition("q")).accept(checker);
        assertEquals(makeSet(2,3,4), checker.getResult());
    }

    @Test
    public void testPreExists() {
        ModelBuilder model = new ModelBuilder();
        model.addStates(1,2,3,4,5,6)
                .addTransition(1,2)
                .addTransition(1,4)
                .addTransition(2,3)
                .addTransition(3,6)
                .addTransition(4,5)
                .addTransition(5,6)
                .addTransition(6,6);

        assertTrue(ModelChecker.isValid(model));

        assertEquals(makeSet(), ModelChecker.preExists(model, makeSet(1)));
        assertEquals(makeSet(1), ModelChecker.preExists(model, makeSet(2)) );
        assertEquals(makeSet(1), ModelChecker.preExists(model, makeSet(4)));
        assertEquals(makeSet(1), ModelChecker.preExists(model, makeSet(2,4)));
        assertEquals(makeSet(1,2), ModelChecker.preExists(model, makeSet(2,3)));
        assertEquals(makeSet(3,5,6), ModelChecker.preExists(model, makeSet(6)));
    }

    @Test
    public void testPreAll() {
        ModelBuilder model = new ModelBuilder();
        model.addStates(1, 2, 3, 4, 5, 6)
                .addTransition(1, 2)
                .addTransition(1, 4)
                .addTransition(2, 3)
                .addTransition(3, 6)
                .addTransition(4, 5)
                .addTransition(5, 6)
                .addTransition(6, 6);

        assertTrue(ModelChecker.isValid(model));

        assertEquals(makeSet(), ModelChecker.preAll(model, makeSet(1)));
        assertEquals(makeSet(), ModelChecker.preAll(model, makeSet(2)));
        assertEquals(makeSet(), ModelChecker.preAll(model, makeSet(4)));
        assertEquals(makeSet(1), ModelChecker.preAll(model, makeSet(2, 4)));
        assertEquals(makeSet(2), ModelChecker.preAll(model, makeSet(2, 3)));
        assertEquals(makeSet(3, 5, 6), ModelChecker.preExists(model, makeSet(6)));
    }

    @Test
    public void testStatementPattern() {
        Launcher launcher = new Launcher();
        CtClass<?> myclass = Launcher.parseClass("class C { void M() { int x = 1; }}");
        CtElement stmt = ((CtMethod<?>)myclass.getMethods().toArray()[0]).getBody().getStatement(0);

        ModelBuilder model = new ModelBuilder();
        model.addStates(1).addTransition(1,1);
        model.addLabel(1, new StatementLabel(stmt));

        assertTrue(ModelChecker.isValid(model));

        ModelChecker checker = new ModelChecker(model);

        PatternBuilder builder = new PatternBuilder();

        stmt.accept(builder);
        PatternNode pattern = builder.getResult();

        new StatementPattern(pattern).accept(checker);
        assertEquals(makeSet(1), checker.getResult());
    }
}
