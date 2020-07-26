package spoon.test.loop.testclasses

class ForLoops {
    var x = 0
    fun shadowedVariable() {
        for(i in 0..10) {
            for(i in 11..20) {
               x = i
            }
        }
    }

    fun emptyFor() {
        for(i in 0..10) {

        }
    }

    fun implicitBlockAssignment() {
        for(i in 0..10)
            x += i
    }

    fun implicitBlockInvocation() {
        for(i in 0..10)
            println(i)
    }

    fun forListIterator() {
        val l = listOf(1,2,3)
        for(e in l) {
            println(e)
        }
    }

    fun forWithOpenRange() {
        for(i in 0 until 10) {

        }
    }

    fun forWithDescendingRange() {
        for(i in 10 downTo 0) {

        }
    }

    fun forWithStep() {
        for(i in 0..10 step 2) {

        }
    }

    fun forWithIndices() {
        val l = listOf(1,2,3)
        for(i in l.indices) {

        }
    }

    fun forWithWithIndex() {
        val l = listOf(1,2,3)
        for((i,n) in l.withIndex()) {

        }
    }


}