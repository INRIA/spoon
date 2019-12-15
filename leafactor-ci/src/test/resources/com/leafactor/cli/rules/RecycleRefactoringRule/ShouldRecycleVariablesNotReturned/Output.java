package test.resources.com.leafactor.cli.rules.RecycleRefactoringRule.ShouldRecycleVariablesNotReturned;




public class Input {
    public TypedArray method1(AttributeSet attrs , int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, new int[]{0}, defStyle, 0);
        String example = a.getString(0);
        final TypedArray b = getNewTypedArray();
        int c = 5;
        if(example.equals("Something")) {
            return a;
        }
        int d = 6;
        return b;
    }
}