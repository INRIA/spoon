package spoon.test.variable.testclasses

class LocalVariables {
    fun m() {
        val l1: Int = 0
        var l2 = l1
        val l3: Int
        var l4: Double

        l3 = l2++
    }

    fun destructured() {
        val (x,y) = Pair(1,2)
        var (a,b,c) = Triple(1,2,3)
    }
}