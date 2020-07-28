package spoon.test.binaryoperator.testclasses

class LogicalOperators {
    val x = true
    val y = false
    val z = true
    fun m() {
        x && y
        y && x
        x && y && z
        x || y
        y || x
        x || y || z
        x && (y || z)
    }
}