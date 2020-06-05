package spoon.test.targeted.testclasses

class SimpleThisAccess {
    var x = 0
    var y = 1

    fun f(x: Int, y: Int) {
        this.x = x
        this.y = y
    }
}