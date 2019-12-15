package test.resources.com.leafactor.cli.rules.RecycleRefactoringRule.VariableReturnedShouldNotRecycle;

public class Input {
    public TypedArray wrong1(AttributeSet attrs , int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, new int[]{0}, defStyle, 0);
        String example = a.getString(0);
        return a;
    }
}