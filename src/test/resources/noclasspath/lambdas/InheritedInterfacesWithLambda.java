public class InheritedInterfacesWithLambda {

    public interface Failing {
        ClickListener listener;
    }

    private static class ExtendedFailClass implements Failing {
        public void test(View itemView) {
            itemView.setOnClickListener(v -> listener.foo());
        }
    }
}
