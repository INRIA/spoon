package test.resources.com.leafactor.cli.rules.RecycleRefactoringRule.VariableClosureShouldNotRecycle;




public class Input {
    public static interface DoSomething<F, T> {
        T convert(F from);
    }

    public void wrong1(AttributeSet attrs , int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, new int[]{0}, defStyle, 0);
        String example = a.getString(0);
        Test b = a.get((DoSomething)(d) -> {
            if(d != null) {
                d.recycle();
            }
            return a;
        });
    }
}