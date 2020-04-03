package spoon.smpl.formula;

/**
 * SetEnv corresponds to the idea of the existentially qualified _v's of the CTL-VW paper.
 * TODO: better documentation
 */
public class SetEnv implements Formula {
    public SetEnv(String metavar, Object value) {
        this.metavar = metavar;
        this.value = value;
    }

    public String getMetavariableName() {
        return metavar;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public void accept(FormulaVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * @return a string representation of this element
     */
    @Override
    public String toString() {
        return "SetEnv(" + metavar + " = " + value.toString() + ")";
    }

    private final String metavar;
    private final Object value;
}
