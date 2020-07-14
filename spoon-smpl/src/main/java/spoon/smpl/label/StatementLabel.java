package spoon.smpl.label;

//import spoon.pattern.Match;
import spoon.reflect.declaration.CtElement;
import spoon.smpl.LabelMatchResultImpl;
import spoon.smpl.formula.Statement;
import spoon.smpl.formula.Predicate;
import spoon.smpl.pattern.*;

import java.util.Map;

/**
 * A StatementLabel is a Label used to associate states with CtElement code
 * elements that can be matched using Statement Formula elements.
 *
 * The intention is for a StatementLabel to contain code that corresponds
 * to a Java statement, but the current implementation does not enforce this.
 */
public class StatementLabel extends CodeElementLabel {
    /**
     * Create a new StatementLabel.
     * @param codeElement Code element
     */
    public StatementLabel(CtElement codeElement) {
        super(codeElement);
    }

    /**
     * Test whether the label matches the given predicate.
     * @param predicate Predicate to test
     * @return True if the predicate is a Statement element whose Pattern matches the code, false otherwise.
     */
    public boolean matches(Predicate predicate) {
        if (predicate instanceof Statement) {
            Statement sp = (Statement) predicate;
            PatternMatcher matcher = new DotsExtPatternMatcher(sp.getPattern());
            codePattern.accept(matcher);

            if (matcher.getResult()) {
                Map<String, Object> metavarBindings = matcher.getParameters();

                if (sp.processMetavariableBindings(metavarBindings)) {
                    matchResults.add(new LabelMatchResultImpl(codeElement, metavarBindings));
                    return true;
                }
            }

            return false;
        } else {
            return super.matches(predicate);
        }
    }
}
