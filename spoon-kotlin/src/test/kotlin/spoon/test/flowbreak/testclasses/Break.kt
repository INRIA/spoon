package spoon.test.flowbreak.testclasses

class Break {
    fun forLoop() {
        for(i in 1..10) {
            break
        }
    }

    fun nestedWhile() {
        outer@ while(true) {
            inner@ while(true) {
                break@inner
                break@outer
            }
        }
    }
}