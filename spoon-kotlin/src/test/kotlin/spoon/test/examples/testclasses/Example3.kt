package spoon.test.examples.testclasses

class Example3() {
    var x = HasOnlyAssignOperators(0)
    val l = listOf(1, 2, 3)

    fun f() {
        for(i in 1..10) {
            x += i%3
            for(j in l) {
                if(x.n != 0 && x.n < 10) {
                    x /= j/i
                    x *= j+1
                }
                if(x.n == 1 || x.n !in l) {
                    x %= j*1
                    x -= j-i
                }
            }
        }
    }
}

class HasOnlyAssignOperators(var n: Int) {

    operator fun plusAssign(k: Int) {
        n += k
    }

    operator fun minusAssign(k: Int) {
        n -= k
    }

    operator fun timesAssign(k: Int) {
        n *= k
    }

    operator fun divAssign(k: Int) {
        n -= k
    }

    operator fun remAssign(k: Int) {
        n %= k
    }
}