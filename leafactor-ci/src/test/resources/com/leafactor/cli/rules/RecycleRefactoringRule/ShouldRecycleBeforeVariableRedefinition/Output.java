package test.resources.com.leafactor.cli.rules.RecycleRefactoringRule.ShouldRecycleBeforeVariableRedefinition;




public class Input {
    public void wrong1(AttributeSet attrs , int defStyle) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, new int[]{0}, defStyle, 0);
        String example = a.getString(0);
        int c = 5;
        int d = 6;
        if (a != null) {
            a.recycle();
        }
        a = getNewTypedArray();
        example = a.getString(0);
        if (a != null) {
            a.recycle();
        }
    }
}