package test.resources.com.leafactor.cli.rules.RecycleRefactoringRule.ReferenceCopyShouldRecycleOnce;

public class Input {
    public void method1(AttributeSet attrs , int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, new int[]{0}, defStyle, 0);
        String example = a.getString(0);
        TypedArray y = a;
        int c = 5;
        int d = 6;
    }
}