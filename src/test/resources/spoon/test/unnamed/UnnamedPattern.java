package spoon.test.unnamed;

class UnnamedPattern {

    record MyRecord(String s) {}

    void pattern(Object o) {
        switch (o) {
            case MyRecord(_) -> {}
            default -> {}
        }
    }
}