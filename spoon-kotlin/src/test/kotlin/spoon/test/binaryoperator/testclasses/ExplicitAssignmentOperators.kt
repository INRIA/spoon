package spoon.test.binaryoperator.testclasses

class ExplicitAssignmentOperators() {
    var x = HasOnlyAssignOperators(0)

    fun f() {
        x += 1
        x -= 2
        x *= 3
        x /= 4
        x %= 5
        x == HasOnlyAssignOperators(6)
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

    operator fun compareTo(other: HasOnlyAssignOperators): Int = n.compareTo(other.n)
}












