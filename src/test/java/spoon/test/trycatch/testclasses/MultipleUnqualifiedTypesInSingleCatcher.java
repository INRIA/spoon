package spoon.test.trycatch.testclasses;

class MultipleUnqualifiedTypesInSingleCatcher {
    public static void main(String[] args) {
        try {
            throw new InterruptedException();
        } catch (InterruptedException | RuntimeException e) {
            // pass
        }
    }
}