package spoon.test.loop.testclasses

class WhileLoops {
    var i = 0

    fun emptyWhile() {
        while(i < 10) {

        }
    }

    fun whileWithInc() {
        while(i < 10) {
            i++
        }
    }

    fun emptyDoWhile() {
        do {

        } while(true)
    }

    fun implicitWhileBody() {
        while(i < 10)
            i += 2
    }

    fun implicitDoWhileBody() {
        do i += 2 while(i < 10)
    }
}