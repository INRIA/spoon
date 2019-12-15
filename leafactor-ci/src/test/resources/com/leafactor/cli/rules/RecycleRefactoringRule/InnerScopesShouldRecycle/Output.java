package test.resources.com.leafactor.cli.rules.RecycleRefactoringRule.InnerScopesShouldRecycle;




public class Input {
//    public void method1(AttributeSet attrs , int defStyle) {
//        final TypedArray a = getContext().obtainStyledAttributes(attrs, new int[]{0}, defStyle, 0);
//        String example = a.getString(0);
//        if(foo()) {
//            a = getNewTypedArray();
//        }
//        int c = 5;
//        int d = 6;
//    }

//    // IDEAL
//    public void method1(AttributeSet attrs , int defStyle) {
//        final TypedArray a = getContext().obtainStyledAttributes(attrs, new int[]{0}, defStyle, 0);
//        String example = a.getString(0);
//        if(foo()) {
//            if(a != null) {
//                a.recycle();
//            }
//            a = getNewTypedArray();
//        }
//        if(a != null) {
//            a.recycle();
//        }
//        int c = 5;
//        int d = 6;
//    }
}