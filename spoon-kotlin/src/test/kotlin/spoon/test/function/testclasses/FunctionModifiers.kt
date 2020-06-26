package spoon.test.function.testclasses

abstract class FunctionModifiers {
    public fun f1() {}

    private fun f2() {}

    abstract fun f3(i: Int): String

    open fun f4() {}

    internal fun f5() {}

    operator infix inline tailrec suspend protected fun plus(i: Int): Int {
       return 0
    }
}