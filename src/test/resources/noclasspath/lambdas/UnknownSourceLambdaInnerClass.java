public class Test {
    /**
     * This class will be built with an '(Unknown source)' as position
     */
    public static class Failing {
        ClickListener listener;
    }

    private static class ExtendedFailClass extends Failing {
        public void test(View itemView) {
            // The difference in behaviour happens if I comment this line out
            itemView.setOnClickListener(v -> listener.onItemClick(getAdapterPosition()));
        }
    }

    /**
     * This class will be built with the right position
     */
    public static class Success {
        ClickListener listener;
    }

    private static class ExtendedSuccessClass extends Success {
        public void test(View itemView) {
            itemView.setOnClickListener(new Runnable());
        }
    }
}
