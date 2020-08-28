package spoon.test.binaryoperator.testclasses

class OperatorComparison {
    fun <S, T: Comparable<S>> m(x: T, y: S) {
        x < y
        x == y
        x > y
    }
}