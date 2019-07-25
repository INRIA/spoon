package com.pkg;

public class InheritedClassesWithLambda1 {

    private static class ExtendedFailClass extends Failing {
        public void test(View itemView) {
            itemView.setOnClickListener(v -> listener.foo());
        }
    }

    public static class Failing {
        ClickListener listener;
    }
}
