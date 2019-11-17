public class InheritedClassesWithLambda2 {

    private static class ExtendedFailClass extends OneMoreClass.Failing {
        public void test(View itemView) {
            itemView.setOnClickListener(v -> listener.foo());
        }
    }

    public static class OneMoreClass
    {
        public static class Failing {
            ClickListener listener;
        }
    }
}