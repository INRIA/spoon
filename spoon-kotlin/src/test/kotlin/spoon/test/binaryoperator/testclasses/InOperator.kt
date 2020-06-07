package spoon.test.binaryoperator.testclasses

class InOperator {
    val l = listOf(1,2,3)
    fun ops() {
        1 in l
        2 !in l
        l.contains(3)
    }
}