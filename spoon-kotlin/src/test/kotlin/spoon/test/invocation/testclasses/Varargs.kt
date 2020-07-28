package spoon.test.invocation.testclasses

class Varargs {


    fun m() {
        f(1, 'a', 'b', 'c', c="c")
        f(2, *charArrayOf('a','b','c'))
        f(c="c", a=3, v= *charArrayOf('a','b','c'))
        f(4, *charArrayOf('a','b','c'), *charArrayOf('d'))
    }

    fun f(a: Int, vararg v: Char, c: String = "") {}
}