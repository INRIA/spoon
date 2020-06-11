package spoon.test.unaryoperator.testclasses

class AllUnaryOperators {
    var h = HasAllUnaryOperators(0)
    var i: Int = 0
    val b = true

    fun allOperators() {
        +h
        -h
        !h
        ++h
        --h
        h++
        h--
    }

    fun intOperators() {
        +i
        -i
        ++i
        --i
        i--
        i++
    }

    fun notOperator() {
        !b
        !!b
    }

}

class HasAllUnaryOperators(var n: Int) {
    operator fun unaryPlus() = HasAllUnaryOperators(+n)
    operator fun unaryMinus() = HasAllUnaryOperators(-n)
    operator fun not() = n == 0
    operator fun inc() = HasAllUnaryOperators(n+1)
    operator fun dec() = HasAllUnaryOperators(n-1)
}