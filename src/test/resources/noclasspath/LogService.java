package spoon.test.prettyprinter.testclasses;

public class LogService<Void> {

    public void main() {
        new ArrayList2().forEach(x -> {
            new Machin<Void>() {

                public Void machin(Void... voids) {
                    return null;
                }
            }.machin(null);
        });
    }
}
