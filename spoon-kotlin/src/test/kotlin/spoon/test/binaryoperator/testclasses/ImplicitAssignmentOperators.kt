package spoon.test.binaryoperator.testclasses

class ImplicitAssignmentOperators {
    var x = HasOnlyNormalOperators(0)

    fun f() {
        x += 1
        x -= 2
        x *= 3
        x /= 4
        x %= 5
        x == HasOnlyNormalOperators(6)
    }
}

class HasOnlyNormalOperators(var n: Int) {
    operator fun plus(k: Int): HasOnlyNormalOperators {
        return HasOnlyNormalOperators(n+1)
    }

    operator fun minus(k: Int): HasOnlyNormalOperators {
        return HasOnlyNormalOperators(n-1)
    }

    operator fun times(k: Int): HasOnlyNormalOperators {
        return HasOnlyNormalOperators(n*1)
    }

    operator fun div(k: Int): HasOnlyNormalOperators {
        return HasOnlyNormalOperators(n/1)
    }

    operator fun rem(k: Int): HasOnlyNormalOperators {
        return HasOnlyNormalOperators(n%1)
    }


}