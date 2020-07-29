package spoon.test.flowbreak.testclasses

class Continue {

    fun forLoop() {
        for(i in 1..10) {
            continue
        }
    }

    fun whileLoop() {
        while(true) {
            continue

        }
    }

    fun doWhile() {
        do {
            continue
        } while(true)
    }

    fun labelledFor() {
        loop@ for(i in 1..10) {
            continue@loop
        }
    }

    fun labelledWhile() {
        loop@ while(true) {
            continue@loop
        }
    }

    fun labelledDoWhile() {
        loop@ do { continue@loop } while(true)
    }

    fun nestedFor() {
        outer@ for(i in 1..10) {
            inner@ for(i in 1..10) {
                continue@outer
                continue@inner
            }
        }
    }

    fun nestedWhile() {
        outer@ while(true) {
            inner@ while(true) {
                continue@outer
                continue@inner
            }
        }
    }

    fun nestDoWhile() {
        outer@ do {
            inner@ do {
                continue@outer
                continue@inner
            } while(true)
        } while(true)
    }
}